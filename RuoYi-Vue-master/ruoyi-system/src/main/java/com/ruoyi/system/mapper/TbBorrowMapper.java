package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TbBorrow;

/**
 * 借阅记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public interface TbBorrowMapper 
{
    /**
     * 查询借阅记录
     * 
     * @param BorrowID 借阅记录主键
     * @return 借阅记录
     */
    public TbBorrow selectTbBorrowByBorrowID(Long BorrowID);

    /**
     * 查询借阅记录列表
     * 
     * @param tbBorrow 借阅记录
     * @return 借阅记录集合
     */
    public List<TbBorrow> selectTbBorrowList(TbBorrow tbBorrow);

    /**
     * 新增借阅记录
     * 
     * @param tbBorrow 借阅记录
     * @return 结果
     */
    public int insertTbBorrow(TbBorrow tbBorrow);

    /**
     * 修改借阅记录
     * 
     * @param tbBorrow 借阅记录
     * @return 结果
     */
    public int updateTbBorrow(TbBorrow tbBorrow);

    /**
     * 删除借阅记录
     * 
     * @param BorrowID 借阅记录主键
     * @return 结果
     */
    public int deleteTbBorrowByBorrowID(Long BorrowID);

    /**
     * 批量删除借阅记录
     * 
     * @param BorrowIDs 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbBorrowByBorrowIDs(Long[] BorrowIDs);
}
