package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TbReadertype;

/**
 * 读者类别Service接口
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public interface ITbReadertypeService 
{
    /**
     * 查询读者类别
     * 
     * @param rdType 读者类别主键
     * @return 读者类别
     */
    public TbReadertype selectTbReadertypeByRdType(Long rdType);

    /**
     * 查询读者类别列表
     * 
     * @param tbReadertype 读者类别
     * @return 读者类别集合
     */
    public List<TbReadertype> selectTbReadertypeList(TbReadertype tbReadertype);

    /**
     * 新增读者类别
     * 
     * @param tbReadertype 读者类别
     * @return 结果
     */
    public int insertTbReadertype(TbReadertype tbReadertype);

    /**
     * 修改读者类别
     * 
     * @param tbReadertype 读者类别
     * @return 结果
     */
    public int updateTbReadertype(TbReadertype tbReadertype);

    /**
     * 批量删除读者类别
     * 
     * @param rdTypes 需要删除的读者类别主键集合
     * @return 结果
     */
    public int deleteTbReadertypeByRdTypes(Long[] rdTypes);

    /**
     * 删除读者类别信息
     * 
     * @param rdType 读者类别主键
     * @return 结果
     */
    public int deleteTbReadertypeByRdType(Long rdType);
}
