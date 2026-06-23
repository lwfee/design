package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TbReader;

/**
 * 读者信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public interface TbReaderMapper 
{
    /**
     * 查询读者信息
     * 
     * @param rdID 读者信息主键
     * @return 读者信息
     */
    public TbReader selectTbReaderByRdID(Long rdID);

    /**
     * 查询读者信息列表
     * 
     * @param tbReader 读者信息
     * @return 读者信息集合
     */
    public List<TbReader> selectTbReaderList(TbReader tbReader);

    /**
     * 新增读者信息
     * 
     * @param tbReader 读者信息
     * @return 结果
     */
    public int insertTbReader(TbReader tbReader);

    /**
     * 修改读者信息
     * 
     * @param tbReader 读者信息
     * @return 结果
     */
    public int updateTbReader(TbReader tbReader);

    /**
     * 删除读者信息
     * 
     * @param rdID 读者信息主键
     * @return 结果
     */
    public int deleteTbReaderByRdID(Long rdID);

    /**
     * 批量删除读者信息
     * 
     * @param rdIDs 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbReaderByRdIDs(Long[] rdIDs);

    public TbReader selectTbReaderByCodeIncludeDeleted(String rdCode);

    // 获取当前最大的证号
    public String selectMaxRdCode();

}


