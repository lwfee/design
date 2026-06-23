package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.TbBook;
import com.ruoyi.system.service.ITbBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    @Autowired
    private ITbBookService bookService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody List<Map<String, String>> chatHistory) { // 👈 1. 接收整个历史列表
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            // 2. 组装发给大模型的完整消息队列
            List<Map<String, String>> messages = new ArrayList<>();

            // 2.1 依旧塞入全局紧箍咒
            messages.add(createMessage("system",
                    "你是一个图书管理系统的 AI 助手。\n" +
                            "【核心铁律】：如果用户让你寻找、添加、修改、删除某本书，你必须且只能调用对应的本地工具！\n" +
                            "绝对不允许在回复中夹带、输出或生成任何类似 `<||DSML||>`、`<tool_calls>` 的内部系统标签！一切以工具返回的真实数据为准！"
            ));

            // 2.2 🔴 把前端传过来的所有历史对话，直接全部追加进去
            messages.addAll(chatHistory);

            requestBody.put("messages", messages);
            requestBody.put("tools", defineLibraryTools());
            requestBody.put("tool_choice", "auto");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            Map body = response.getBody();
            Map firstChoice = (Map) ((List) body.get("choices")).get(0);
            Map messageResult = (Map) firstChoice.get("message");

            // 🟢 升级版：支持批量、多工具同时调用的循环逻辑
            if (messageResult.containsKey("tool_calls")) {
                System.out.println("====== 🎉 抓到了！AI 触发了批量自动化操作！ ======");
                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) messageResult.get("tool_calls");

                // 1. 先把 AI 的这个包含多个 tool_calls 的消息整体塞进历史队列
                messages.add(messageResult);

                // 2. 🔴 核心修复：用 for 循环遍历每一个工具调用任务
                for (Map<String, Object> toolCall : toolCalls) {
                    String toolCallId = (String) toolCall.get("id");
                    Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                    String functionName = (String) function.get("name");
                    String functionArgs = (String) function.get("arguments");

                    Map<String, Object> argsMap = objectMapper.readValue(functionArgs, Map.class);
                    String executionResult = "";

                    // ---------------- 【分支 1：查】 ----------------
                    if ("queryBooks".equals(functionName)) {
                        String title = (String) argsMap.get("title");
                        System.out.println("👉 批量处理 - 执行【查询】, 关键词: " + title);

                        TbBook queryParam = new TbBook();
                        queryParam.setBkName(title);
                        List<TbBook> list = bookService.selectTbBookList(queryParam);

                        executionResult = (list == null || list.isEmpty()) ?
                                "【数据库返回】：未找到任何匹配的书籍，结果为空 []" :
                                "【数据库返回】：" + objectMapper.writeValueAsString(list);
                    }
                    // ---------------- 【分支 2：增】 ----------------
                    else if ("addBook".equals(functionName)) {
                        String title = (String) argsMap.get("title");
                        String author = (String) argsMap.get("author");
                        System.out.println("👉 批量处理 - 执行【新增】, 书名: " + title + ", 作者: " + author);

                        TbBook newBook = new TbBook();
                        newBook.setBkName(title);
                        newBook.setBkAuthor(author != null ? author : "未知作者");
                        int rows = bookService.insertTbBook(newBook);

                        executionResult = rows > 0 ? "【数据库返回】：图书《" + title + "》入库成功！" : "【数据库返回】：图书入库失败。";
                    }
                    // ---------------- 【分支 3：改】 ----------------
                    else if ("updateBook".equals(functionName)) {
                        Integer id = (Integer) argsMap.get("id");
                        String title = (String) argsMap.get("title");
                        String author = (String) argsMap.get("author");
                        System.out.println("👉 批量处理 - 执行【修改】, ID: " + id);

                        TbBook updateParam = new TbBook();
                        updateParam.setBkID(Long.valueOf(id));
                        if (title != null) updateParam.setBkName(title);
                        if (author != null) updateParam.setBkAuthor(author);

                        int rows = bookService.updateTbBook(updateParam);
                        executionResult = rows > 0 ? "【数据库返回】：图书信息修改成功！" : "【数据库返回】：修改失败。";
                    }
                    // ---------------- 【分支 4：删】 ----------------
                    else if ("deleteBook".equals(functionName)) {
                        Integer id = (Integer) argsMap.get("id");
                        System.out.println("👉 批量处理 - 执行【删除】, ID: " + id);

                        int rows = bookService.deleteTbBookByBkID(Long.valueOf(id));
                        executionResult = rows > 0 ? "【数据库返回】：该图书已被永久从系统删除！" : "【数据库返回】：删除失败。";
                    }

                    // 🔴 核心修复：每一次循环，都必须把当前任务的执行结果用对应的 toolCallId 包装，并追加到 messages 中
                    messages.add(createToolMessage(toolCallId, executionResult));
                }

                // 3. 当所有批量工具都执行完毕后，一次性打包发回给大模型进行最终的“人话总结”
                requestBody.put("messages", messages);
                requestBody.remove("tools");

                ResponseEntity<Map> finalResponse = restTemplate.postForEntity(apiUrl, new HttpEntity<>(requestBody, headers), Map.class);
                Map finalBody = finalResponse.getBody();
                String aiFinalAnswer = (String) ((Map)((Map)((List) finalBody.get("choices")).get(0)).get("message")).get("content");

                return AjaxResult.success("操作成功", aiFinalAnswer);
            }

            return AjaxResult.success("操作成功", messageResult.get("content"));

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("操作失败：" + e.getMessage());
        }
    }
    /**
     * 工具箱定义
     */
    private List<Map<String, Object>> defineLibraryTools() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // 1. 工具：查
        Map<String, Object> queryTool = new HashMap<>();
        queryTool.put("type", "function");
        Map<String, Object> qFunction = new HashMap<>();
        qFunction.put("name", "queryBooks");
        qFunction.put("description", "查询系统数据库，来验证某本图书是否存在。");
        Map<String, Object> qParameters = new HashMap<>();
        qParameters.put("type", "object");
        Map<String, Object> qProperties = new HashMap<>();
        Map<String, String> titleMap = new HashMap<>();
        titleMap.put("type", "string");
        titleMap.put("description", "要查询的书名关键字");
        qProperties.put("title", titleMap);
        qParameters.put("properties", qProperties);
        qFunction.put("parameters", qParameters);
        queryTool.put("function", qFunction);
        tools.add(queryTool);

        // 2. 工具：增
        Map<String, Object> addTool = new HashMap<>();
        addTool.put("type", "function");
        Map<String, Object> aFunction = new HashMap<>();
        aFunction.put("name", "addBook");
        aFunction.put("description", "向系统中录入、添加一本全新的图书。");
        Map<String, Object> aParameters = new HashMap<>();
        aParameters.put("type", "object");
        Map<String, Object> aProperties = new HashMap<>();
        Map<String, String> aTitle = new HashMap<>();
        aTitle.put("type", "string");
        aTitle.put("description", "新书的书名");
        Map<String, String> aAuthor = new HashMap<>();
        aAuthor.put("type", "string");
        aAuthor.put("description", "新书的作者");
        aProperties.put("title", aTitle);
        aProperties.put("author", aAuthor);
        aParameters.put("properties", aProperties);
        aParameters.put("required", Arrays.asList("title"));
        aFunction.put("parameters", aParameters);
        addTool.put("function", aFunction);
        tools.add(addTool);

        // 3. 工具：改
        Map<String, Object> updateTool = new HashMap<>();
        updateTool.put("type", "function");
        Map<String, Object> uFunction = new HashMap<>();
        uFunction.put("name", "updateBook");
        uFunction.put("description", "修改已存在图书的信息。必须提供图书的ID。");
        Map<String, Object> uParameters = new HashMap<>();
        uParameters.put("type", "object");
        Map<String, Object> uProperties = new HashMap<>();
        Map<String, String> uId = new HashMap<>();
        uId.put("type", "integer");
        uId.put("description", "要修改的图书主键ID");
        Map<String, String> uTitle = new HashMap<>();
        uTitle.put("type", "string");
        uTitle.put("description", "修改后的新书名");
        Map<String, String> uAuthor = new HashMap<>();
        uAuthor.put("type", "string");
        uAuthor.put("description", "修改后的新作者");
        uProperties.put("id", uId);
        uProperties.put("title", uTitle);
        uProperties.put("author", uAuthor);
        uParameters.put("properties", uProperties);
        uParameters.put("required", Arrays.asList("id"));
        uFunction.put("parameters", uParameters);
        updateTool.put("function", uFunction);
        tools.add(updateTool);

        // 4. 工具：删
        Map<String, Object> deleteTool = new HashMap<>();
        deleteTool.put("type", "function");
        Map<String, Object> dFunction = new HashMap<>();
        dFunction.put("name", "deleteBook");
        dFunction.put("description", "从系统数据库中删除某本图书。必须提供图书的ID。");
        Map<String, Object> dParameters = new HashMap<>();
        dParameters.put("type", "object");
        Map<String, Object> dProperties = new HashMap<>();
        Map<String, String> dId = new HashMap<>();
        dId.put("type", "integer");
        dId.put("description", "要删除的图书主键ID");
        dProperties.put("id", dId);
        dParameters.put("properties", dProperties);
        dParameters.put("required", Arrays.asList("id"));
        dFunction.put("parameters", dParameters);
        deleteTool.put("function", dFunction);
        tools.add(deleteTool);

        return tools;
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }

    private Map<String, String> createToolMessage(String toolCallId, String content) {
        Map<String, String> msg = new HashMap<>();
        msg.put("role", "tool");
        msg.put("tool_call_id", toolCallId);
        msg.put("content", content);
        return msg;
    }
}