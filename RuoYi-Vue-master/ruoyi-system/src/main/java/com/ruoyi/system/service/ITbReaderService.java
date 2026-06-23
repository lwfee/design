package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TbReader;
/**
 * 读者信息Service接口
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public interface ITbReaderService 
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
     * 批量删除读者信息
     * 
     * @param rdIDs 需要删除的读者信息主键集合
     * @return 结果
     */
    public int deleteTbReaderByRdIDs(Long[] rdIDs);

    /**
     * 删除读者信息信息
     * 
     * @param rdID 读者信息主键
     * @return 结果
     */
    public int deleteTbReaderByRdID(Long rdID);

    public int recoverReader(Long userId);

    public String getNextRdCode();

    public int reissueCard(Long rdID);

}
