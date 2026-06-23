package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TbReadertype;

/**
 * 读者类别Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public interface TbReadertypeMapper 
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
     * 删除读者类别
     * 
     * @param rdType 读者类别主键
     * @return 结果
     */
    public int deleteTbReadertypeByRdType(Long rdType);

    /**
     * 批量删除读者类别
     * 
     * @param rdTypes 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbReadertypeByRdTypes(Long[] rdTypes);
}
