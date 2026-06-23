package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TbBook;

/**
 * 图书信息Service接口
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public interface ITbBookService 
{
    /**
     * 查询图书信息
     * 
     * @param bkID 图书信息主键
     * @return 图书信息
     */
    public TbBook selectTbBookByBkID(Long bkID);

    /**
     * 查询图书信息列表
     * 
     * @param tbBook 图书信息
     * @return 图书信息集合
     */
    public List<TbBook> selectTbBookList(TbBook tbBook);

    /**
     * 新增图书信息
     * 
     * @param tbBook 图书信息
     * @return 结果
     */
    public int insertTbBook(TbBook tbBook);

    /**
     * 修改图书信息
     * 
     * @param tbBook 图书信息
     * @return 结果
     */
    public int updateTbBook(TbBook tbBook);

    /**
     * 批量删除图书信息
     * 
     * @param bkIDs 需要删除的图书信息主键集合
     * @return 结果
     */
    public int deleteTbBookByBkIDs(Long[] bkIDs);

    /**
     * 删除图书信息信息
     * 
     * @param bkID 图书信息主键
     * @return 结果
     */
    public int deleteTbBookByBkID(Long bkID);
}
