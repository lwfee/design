package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TbBookMapper;
import com.ruoyi.system.domain.TbBook;
import com.ruoyi.system.service.ITbBookService;

/**
 * 图书信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
@Service
public class TbBookServiceImpl implements ITbBookService 
{
    @Autowired
    private TbBookMapper tbBookMapper;

    /**
     * 查询图书信息
     * 
     * @param bkID 图书信息主键
     * @return 图书信息
     */
    @Override
    public TbBook selectTbBookByBkID(Long bkID)
    {
        return tbBookMapper.selectTbBookByBkID(bkID);
    }

    /**
     * 查询图书信息列表
     * 
     * @param tbBook 图书信息
     * @return 图书信息
     */
    @Override
    public List<TbBook> selectTbBookList(TbBook tbBook)
    {
        return tbBookMapper.selectTbBookList(tbBook);
    }

    /**
     * 新增图书信息
     * 
     * @param tbBook 图书信息
     * @return 结果
     */
    @Override
    public int insertTbBook(TbBook tbBook)
    {
        return tbBookMapper.insertTbBook(tbBook);
    }

    /**
     * 修改图书信息
     * 
     * @param tbBook 图书信息
     * @return 结果
     */
    @Override
    public int updateTbBook(TbBook tbBook)
    {
        return tbBookMapper.updateTbBook(tbBook);
    }

    /**
     * 批量删除图书信息
     * 
     * @param bkIDs 需要删除的图书信息主键
     * @return 结果
     */
    @Override
    public int deleteTbBookByBkIDs(Long[] bkIDs)
    {
        return tbBookMapper.deleteTbBookByBkIDs(bkIDs);
    }

    /**
     * 删除图书信息信息
     * 
     * @param bkID 图书信息主键
     * @return 结果
     */
    @Override
    public int deleteTbBookByBkID(Long bkID)
    {
        return tbBookMapper.deleteTbBookByBkID(bkID);
    }
}
