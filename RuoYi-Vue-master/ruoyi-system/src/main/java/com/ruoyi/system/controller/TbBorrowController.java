package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.TbBorrow;
import com.ruoyi.system.service.ITbBorrowService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 借阅记录Controller
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
@RestController
@RequestMapping("/system/borrow")
public class TbBorrowController extends BaseController
{
    @Autowired
    private ITbBorrowService tbBorrowService;

    /**
     * 查询借阅记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:borrow:list')")
    @GetMapping("/list")
    public TableDataInfo list(TbBorrow tbBorrow)
    {

        List<TbBorrow> list = tbBorrowService.selectTbBorrowList(tbBorrow);
        return getDataTable(list);
    }

    /**
     * 导出借阅记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:borrow:export')")
    @Log(title = "借阅记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TbBorrow tbBorrow)
    {
        List<TbBorrow> list = tbBorrowService.selectTbBorrowList(tbBorrow);
        ExcelUtil<TbBorrow> util = new ExcelUtil<TbBorrow>(TbBorrow.class);
        util.exportExcel(response, list, "借阅记录数据");
    }

    /**
     * 获取借阅记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:borrow:query')")
    @GetMapping(value = "/{BorrowID}")
    public AjaxResult getInfo(@PathVariable("BorrowID") Long BorrowID)
    {
        return success(tbBorrowService.selectTbBorrowByBorrowID(BorrowID));
    }

    /**
     * 新增借阅记录
     */
    @PreAuthorize("@ss.hasPermi('system:borrow:add')")
    @Log(title = "借阅记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TbBorrow tbBorrow)
    {
        return toAjax(tbBorrowService.insertTbBorrow(tbBorrow));
    }

    /**
     * 修改借阅记录
     */
    @PreAuthorize("@ss.hasPermi('system:borrow:edit')")
    @Log(title = "借阅记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TbBorrow tbBorrow)
    {
        return toAjax(tbBorrowService.updateTbBorrow(tbBorrow));
    }

    /**
     * 删除借阅记录
     */
    @PreAuthorize("@ss.hasPermi('system:borrow:remove')")
    @Log(title = "借阅记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{BorrowIDs}")
    public AjaxResult remove(@PathVariable Long[] BorrowIDs)
    {
        return toAjax(tbBorrowService.deleteTbBorrowByBorrowIDs(BorrowIDs));
    }

    /**
     * 点击“借书”按钮时调用
     */
    @PostMapping("/doBorrow")
    public com.ruoyi.common.core.domain.AjaxResult doBorrow(@RequestBody TbBorrow tbBorrow)
    {
        try {
            return toAjax(tbBorrowService.borrowBook(tbBorrow));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/selfBorrow")
    public AjaxResult selfBorrow(@RequestBody TbBorrow tbBorrow) {
        // 1. 获取当前登录的用户名 (例如 "r2024001")
        String username = SecurityUtils.getUsername();

        // 2. 解析出证号 (去掉前缀 "r")
        // 如果你之前没加前缀，就直接用 username
        String rdCode = username.replace("rd", "");

        // 3. 填入证号，让 Service 层去查具体的 ID
        tbBorrow.setRdCode(rdCode);

        // 4. 调用之前写好的核心借书逻辑
        return toAjax(tbBorrowService.borrowBook(tbBorrow));
    }

    /**
     * 自助还书 (读者只能还自己的书)
     */


    @PostMapping("/selfReturn")
    public AjaxResult selfReturn(@RequestBody TbBorrow tbBorrow) {
        // 这里其实可以直接调用 returnBook，
        // 但为了安全，你可以在 Service 里加个判断：这条借阅记录的 rdID 是不是等于当前登录人的 rdID
        return toAjax(tbBorrowService.returnBook(tbBorrow));
    }

}


