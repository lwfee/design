<template>
  <div class="app-container" style="background-color: #f5f7fa; min-height: calc(100vh - 84px);">
    <el-row :gutter="20" justify="center" type="flex">
      <el-col :xs="24" :sm="20" :md="16" :lg="14">

        <el-card class="box-card" style="height: 650px; display: flex; flex-direction: column; border-radius: 8px;">
          <div slot="header" class="clearfix" style="text-align: center;">
            <span style="font-size: 18px; font-weight: bold; color: #303133;">
              🤖 图书管理系统 AI 智能全能助手
            </span>
            <p style="margin: 5px 0 0 0; font-size: 12px; color: #909399;">
              支持通过指令进行图书的【增、删、改、查】操作
            </p>
          </div>

          <div class="chat-box" ref="chatBox" style="flex: 1; overflow-y: auto; padding: 15px; border-bottom: 1px solid #ebeef5; height: 460px;">
            <div v-for="(item, index) in chatList" :key="index" :class="['msg-row', item.role === 'user' ? 'msg-user' : 'msg-ai']" style="margin-bottom: 15px; display: flex;">

              <el-avatar v-if="item.role === 'assistant'" icon="el-icon-cpu" size="small" style="background-color: #409EFF; margin-right: 10px; flex-shrink: 0;"></el-avatar>

              <div class="msg-content" :style="getMsgStyle(item.role)">
                <div style="white-space: pre-wrap; word-break: break-all; line-height: 1.6;">{{ item.content }}</div>
              </div>

              <el-avatar v-if="item.role === 'user'" icon="el-icon-user-solid" size="small" style="background-color: #67C23A; margin-left: 10px; flex-shrink: 0;"></el-avatar>

            </div>
          </div>

          <div style="padding-top: 15px;">
            <el-input
              type="text"
              placeholder="试试说：‘帮我查一下有没有鲁迅写的书’ 或 ‘入库一本新书叫《三体》’"
              v-model="inputMsg"
              @keyup.enter.native="handleSend"
              :disabled="loading"
            >
              <el-button slot="append" type="primary" icon="el-icon-position" @click="handleSend" :loading="loading">
                {{ loading ? '思考中' : '发送' }}
              </el-button>
            </el-input>
          </div>

        </el-card>

      </el-col>
    </el-row> </div>
</template>
<script>
// 🔴 注意：这里的路径要对应你上面创建 ai.js 的真实路径
import { sendAiChat } from "@/api/ai";

export default {
  name: "AiAssistant",
  data() {
    return {
      inputMsg: "",
      loading: false,
      chatList: [
        { role: "assistant", content: "您好！我是您的系统 AI 助手。我已经连接了图书馆数据库，您可以直接对我说：\n1. “帮我查一下有没有《西游记》”\n2. “新增一本名叫《朝花夕拾》的书，作者是鲁迅”" }
      ]
    };
  },
  methods: {
    // 发送消息
    handleSend() {
      if (!this.inputMsg.trim()) return;

      const userText = this.inputMsg;
      // 1. 先把用户的话加进历史
      this.chatList.push({ role: "user", content: userText });
      this.inputMsg = "";
      this.loading = true;
      this.scrollToBottom();

      // 2. 🔴 关键改动：传入完整的对话历史 this.chatList
      sendAiChat(this.chatList).then(res => {
        this.chatList.push({ role: "assistant", content: res.data });
      }).catch(err => {
        this.$message.error("AI 助手连接失败");
      }).finally(() => {
        this.loading = false;
        this.scrollToBottom();
      });
    },
    // 气泡样式控制
    getMsgStyle(role) {
      if (role === 'user') {
        return {
          backgroundColor: '#95ec69',
          color: '#000',
          padding: '10px 14px',
          borderRadius: '8px 0px 8px 8px',
          maxWidth: '70%',
          marginLeft: 'auto'
        }
      } else {
        return {
          backgroundColor: '#fff',
          color: '#303133',
          padding: '10px 14px',
          borderRadius: '0px 8px 8px 8px',
          maxWidth: '70%',
          boxShadow: '0 2px 12px 0 rgba(0,0,0,0.05)'
        }
      }
    },
    // 聊天框自动滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const chatBox = this.$refs.chatBox;
        chatBox.scrollTop = chatBox.scrollHeight;
      });
    }
  }
};
</script>

<style scoped>
/* 隐藏滚动条，但保留滚动功能 */
.chat-box::-webkit-scrollbar {
  width: 6px;
}
.chat-box::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}
</style>
