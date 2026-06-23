package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TbBook;
import com.ruoyi.system.domain.TbReader;
import com.ruoyi.system.domain.TbReadertype;
import com.ruoyi.system.mapper.TbReaderMapper;
import com.ruoyi.system.mapper.TbReadertypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TbBorrowMapper;
import com.ruoyi.system.domain.TbBorrow;
import com.ruoyi.system.service.ITbBorrowService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 借阅记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
@Service
public class TbBorrowServiceImpl implements ITbBorrowService
{
    @Autowired
    private TbBorrowMapper tbBorrowMapper;

    @Autowired
    private TbReadertypeMapper tbReaderTypeMapper; // 👈 必须引入这个，才能查类别表

    @Autowired
    private com.ruoyi.system.mapper.TbBookMapper tbBookMapper;

    @Autowired
    private com.ruoyi.system.mapper.TbReaderMapper tbReaderMapper;

    /**
     * 查询借阅记录
     *
     * @param BorrowID 借阅记录主键
     * @return 借阅记录
     */
    @Override
    public TbBorrow selectTbBorrowByBorrowID(Long BorrowID)
    {
        return tbBorrowMapper.selectTbBorrowByBorrowID(BorrowID);
    }

    /**
     * 查询借阅记录列表
     *
     * @param tbBorrow 借阅记录
     * @return 借阅记录
     */
    @Override
    public List<TbBorrow> selectTbBorrowList(TbBorrow tbBorrow)
    {
        // 1. 获取当前登录的账号
        String username = SecurityUtils.getUsername();

        // 2. 权限判断与过滤
        // ⚠️ 修正：之前写的是 startsWith("rd") 但截取是用 substring(1)
        // 如果前缀是 "r"，就写 "r"；如果是 "rd"，截取要用 substring(2)
        // 这里假设你用的是 "r" (比如 r202401)
        if (username != null && username.startsWith("rd")) {

            // 去掉 'r' 得到证号
            String myRdCode = username.substring(2);

            TbReader readerParam = new TbReader();
            readerParam.setRdCode(myRdCode);
            List<TbReader> readers = tbReaderMapper.selectTbReaderList(readerParam);

            if (readers != null && readers.size() > 0) {
                // 找到读者ID，强制设置查询条件
                tbBorrow.setRdID(readers.get(0).getRdID());
            } else {
                // 查不到人，直接返回空
                return new ArrayList<>();
            }
        }
        com.ruoyi.common.utils.PageUtils.startPage();
        // 3. 执行查询 (这一步查出来的结果，rdCode 是空的)
        List<TbBorrow> borrowList = tbBorrowMapper.selectTbBorrowList(tbBorrow);

        // ============================================================
        // 🛠️ 关键补充：Java 层面手动补全数据 (解决前端不显示的问题)
        // ============================================================
        for (TbBorrow item : borrowList) {

            // 补全读者信息 (根据 rdID 查 tb_reader)
            if (item.getRdID() != null) {
                TbReader reader = tbReaderMapper.selectTbReaderByRdID(item.getRdID());
                if (reader != null) {
                    item.setRdCode(reader.getRdCode()); // ✅ 填入证号

                }
            }

        }

        return borrowList;
    }

    /**
     * 新增借阅记录
     * 
     * @param tbBorrow 借阅记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertTbBorrow(TbBorrow tbBorrow) {
        // 1. 设置借阅时间
        tbBorrow.setLdDateOut(DateUtils.getNowDate());

        // 2. 校验证号
        if (StringUtils.isEmpty(tbBorrow.getRdCode())) {
            return tbBorrowMapper.insertTbBorrow(tbBorrow);
        }

        // 3. 查读者
        TbReader queryReader = new TbReader();
        queryReader.setRdCode(tbBorrow.getRdCode());
        List<TbReader> readerList = tbReaderMapper.selectTbReaderList(queryReader);

        if (readerList == null || readerList.size() == 0) {
            throw new ServiceException("找不到读者：" + tbBorrow.getRdCode());
        }

        TbReader foundReader = readerList.get(0);
        Long rdId = foundReader.getRdID();

        // ============================================================
        // 🕵️‍♂️ 步骤 A：获取【真实】的当前已借数量 (查借阅表)
        // ============================================================
        TbBorrow countParam = new TbBorrow();
        countParam.setRdID(rdId);
        countParam.setIsHasReturn(0L); // 0 代表未还
        List<TbBorrow> unReturnedList = tbBorrowMapper.selectTbBorrowList(countParam);

        int currentRealQty = unReturnedList.size();
        System.out.println("【调试】当前实际未还数量：" + currentRealQty);

        // ============================================================
        // 🕵️‍♂️ 步骤 B：获取最大借阅限额 (查类别表)
        // ============================================================
        Long limitQty = 0L;

        // 1. 获取读者的类别ID
        Long typeId = foundReader.getRdType();

        if (typeId != null) {
            // 2. ✅ 使用你截图里的那个方法名！
            // 注意：这里返回的类型可能是 TbReadertype (注意大小写)
            TbReadertype type = tbReaderTypeMapper.selectTbReadertypeByRdType(typeId);

            if (type != null) {
                // 3. ✅ 从类别对象里拿限额
                limitQty = type.getCanLendQty();
            }
        }
        System.out.println("【调试】该读者的限额是：" + limitQty);

        // ============================================================
        // 🛑 步骤 C：拦截校验
        // ============================================================
        if (limitQty > 0 && currentRealQty >= limitQty) {
            throw new ServiceException("借阅失败！您已借 " + currentRealQty + " 本，最大限额 " + limitQty + " 本。");
        }

        // ============================================================
        // 💾 步骤 D：保存借阅记录
        // ============================================================
        tbBorrow.setRdID(rdId);
        int rows = tbBorrowMapper.insertTbBorrow(tbBorrow);

        // ============================================================
        // 📈 步骤 E：同步更新读者表 (可选，为了数据好看)
        // ============================================================
        if (rows > 0) {
            foundReader.setRdBorrowQty((long) (currentRealQty + 1));
            tbReaderMapper.updateTbReader(foundReader);
        }

        return rows;
    }
    /**
     * 修改借阅记录
     * 
     * @param tbBorrow 借阅记录
     * @return 结果
     */
    @Override
    public int updateTbBorrow(TbBorrow tbBorrow)
    {
        return tbBorrowMapper.updateTbBorrow(tbBorrow);
    }

    /**
     * 批量删除借阅记录
     * 
     * @param BorrowIDs 需要删除的借阅记录主键
     * @return 结果
     */
    @Override
    public int deleteTbBorrowByBorrowIDs(Long[] BorrowIDs)
    {
        return tbBorrowMapper.deleteTbBorrowByBorrowIDs(BorrowIDs);
    }

    /**
     * 删除借阅记录信息
     * 
     * @param BorrowID 借阅记录主键
     * @return 结果
     */
    @Override
    public int deleteTbBorrowByBorrowID(Long BorrowID)
    {
        return tbBorrowMapper.deleteTbBorrowByBorrowID(BorrowID);
    }

    /**
     * 办理借书业务
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public int borrowBook(TbBorrow tbBorrow) {

        if (StringUtils.isNotEmpty(tbBorrow.getRdCode())) {
            System.out.println("【借书办理】收到证号：" + tbBorrow.getRdCode() + "，正在查找读者...");
            com.ruoyi.system.domain.TbReader queryReader = new com.ruoyi.system.domain.TbReader();
            queryReader.setRdCode(tbBorrow.getRdCode());
            List<com.ruoyi.system.domain.TbReader> list = tbReaderMapper.selectTbReaderList(queryReader);

            if (list != null && list.size() > 0) {
                com.ruoyi.system.domain.TbReader foundReader = list.get(0);
                tbBorrow.setRdID(foundReader.getRdID());
                System.out.println("✅ 身份确认成功，读者ID为：" + foundReader.getRdID());
            } else {
                throw new RuntimeException("借书失败：系统里找不到证号为 [" + tbBorrow.getRdCode() + "] 的读者！");
            }
        } else {
            if (tbBorrow.getRdID() == null) {
                throw new RuntimeException("借书失败：请输入读者证号！");
            }
        }

        // 1. 获取参数
        Long bkID = tbBorrow.getBkID();
        Long RdID = tbBorrow.getRdID();

        // 2. 查询图书和读者
        com.ruoyi.system.domain.TbBook book = tbBookMapper.selectTbBookByBkID(bkID);
        com.ruoyi.system.domain.TbReader reader = tbReaderMapper.selectTbReaderByRdID(RdID);

        // 3. 校验：书必须存在且状态为"在馆"
        if (book == null || !"0".equals(book.getBkStatus())) {
            throw new RuntimeException("借书失败：图书不存在或已被借出！");
        }

        // 4. 校验：读者必须存在且状态为"有效"
        if (reader == null || !"1".equals(reader.getRdStatus())) {
            throw new RuntimeException("借书失败：读者证号无效或已挂失！");
        }

        // =====================================================================
        // 🚀【新增校验】这里就是“安检口”：检查已借数量是否超标
        // =====================================================================
        int maxLimit = 3; // 这里设置最大借书量（也可以去 tb_readertype 表里查）

        // 获取当前已借数量（防止 null 报空指针）
        Long currentQtyCheck = reader.getRdBorrowQty() == null ? 0L : reader.getRdBorrowQty();

        if (currentQtyCheck >= maxLimit) {
            // 如果 已经借的 >= 3，直接报错，程序终止，不往下走了
            throw new RuntimeException("借书失败：该读者已借 " + currentQtyCheck + " 本书，达到上限(" + maxLimit + "本)！请先归还。");
        }
        // =====================================================================


        // 5. 业务处理 A：插入借阅记录
        tbBorrow.setLdDateOut(com.ruoyi.common.utils.DateUtils.getNowDate());
        tbBorrow.setLdDateRetPlan(com.ruoyi.common.utils.DateUtils.addDays(tbBorrow.getLdDateOut(), 30));
        tbBorrow.setIsHasReturn(0L);
        int rows = tbBorrowMapper.insertTbBorrow(tbBorrow);

        // 6. 业务处理 B：更新图书状态为"借出"
        book.setBkStatus("1");
        tbBookMapper.updateTbBook(book);

        // 7. 业务处理 C：读者已借数量 + 1
        // (逻辑严谨点：使用刚才处理过 null 的变量 + 1)
        reader.setRdBorrowQty(currentQtyCheck + 1);
        tbReaderMapper.updateTbReader(reader);

        return rows;
    }
    @Override
    public int returnBook(TbBorrow tbBorrow) {
        // 1. 根据借阅记录的主键ID，查出详情
        TbBorrow dbBorrow = tbBorrowMapper.selectTbBorrowByBorrowID(tbBorrow.getBorrowID());

        if (dbBorrow == null) {
            throw new ServiceException("借阅记录不存在！");
        }
        if (dbBorrow.getIsHasReturn() != null && dbBorrow.getIsHasReturn() == 1L) {
            throw new ServiceException("这本书已经还过了，请勿重复操作！");
        }

        // 2. 更新借阅记录 (设置为已还)
        dbBorrow.setLdDateRetAct(DateUtils.getNowDate());
        dbBorrow.setIsHasReturn(1L);
        int rows = tbBorrowMapper.updateTbBorrow(dbBorrow);

        // 3. 图书归位
        TbBook book = tbBookMapper.selectTbBookByBkID(dbBorrow.getBkID());
        if (book != null) {
            book.setBkStatus("0");
            tbBookMapper.updateTbBook(book);
        }

        // 4. 释放额度
        TbReader reader = tbReaderMapper.selectTbReaderByRdID(dbBorrow.getRdID());
        if (reader != null) {
            Long currentQty = reader.getRdBorrowQty();
            if (currentQty != null && currentQty > 0) {
                reader.setRdBorrowQty(currentQty - 1);
                tbReaderMapper.updateTbReader(reader);
            }
        }

        return rows;
    }

}
