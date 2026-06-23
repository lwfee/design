package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TbReaderMapper;
import com.ruoyi.system.domain.TbReader;
import com.ruoyi.system.service.ITbReaderService;
import com.ruoyi.common.core.domain.entity.SysUser; // 引入系统用户实体
import com.ruoyi.system.service.ISysUserService;   // 引入系统用户服务
import com.ruoyi.common.utils.SecurityUtils;      // 引入密码加密工具
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import com.ruoyi.common.core.domain.entity.SysUser;

/**
 * 读者信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
@Service
public class TbReaderServiceImpl implements ITbReaderService {
    @Autowired
    private TbReaderMapper tbReaderMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysUserMapper sysUserMapper;


    /**
     * 查询读者信息
     *
     * @param rdID 读者信息主键
     * @return 读者信息
     */
    @Override
    public TbReader selectTbReaderByRdID(Long rdID) {
        return tbReaderMapper.selectTbReaderByRdID(rdID);
    }

    /**
     * 查询读者信息列表
     *
     * @param tbReader 读者信息
     * @return 读者信息
     */
    @Override
    public List<TbReader> selectTbReaderList(TbReader tbReader) {
        // 1. 获取当前登录账号
        String username = SecurityUtils.getUsername();

        if (StringUtils.isNotNull(username) && username.startsWith("rd")) {

            // 3. 解析出证号 (例如 "r2024001" -> "2024001")
            String myRdCode = username.substring(2);

            // 4. 强制过滤：只查这个证号的读者信息
            tbReader.setRdCode(myRdCode);
        }

        // 5. 执行查询
        return tbReaderMapper.selectTbReaderList(tbReader);
    }

    /**
     * 新增读者信息
     *
     * @param tbReader 读者信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTbReader(TbReader tbReader) {
        String rdCode = tbReader.getRdCode();
        String userName = "rd" + rdCode;

        // 1. 校验读者编号是否冲突 (此时数据库只有活跃数据)
        // 使用普通的查询即可，因为被删除的数据物理上已经不存在了
        TbReader existReader = tbReaderMapper.selectTbReaderByCodeIncludeDeleted(rdCode);
        if (existReader != null) {
            throw new RuntimeException("创建失败：读者证号 " + rdCode + " 已存在！");
        }

        // 2. 插入读者记录
        int rows = tbReaderMapper.insertTbReader(tbReader);

        if (rows > 0) {
            try {
                // 3. 检查系统用户表（预防万一有手动删除残留）
                SysUser existUser = sysUserService.selectUserByUserName(userName);
                if (existUser != null) {
                    // 如果用户表还残留（可能是之前没删干净），直接更新它
                    existUser.setNickName(tbReader.getRdName());
                    existUser.setDelFlag("0"); // 确保状态正常
                    sysUserService.updateUser(existUser);
                } else {
                    // 正常创建全新账号
                    SysUser user = new SysUser();
                    user.setUserName(userName);
                    user.setNickName(tbReader.getRdName());
                    user.setPhonenumber(tbReader.getRdPhone());
                    user.setSex("男".equals(tbReader.getRdSex()) ? "0" : "1");
                    user.setPassword(SecurityUtils.encryptPassword("123456"));
                    user.setRoleIds(new Long[]{103L}); // 读者角色ID
                    sysUserService.insertUser(user);
                }
            } catch (Exception e) {
                throw new RuntimeException("关联系统账号失败：" + e.getMessage());
            }
        }
        return rows;
    }

    /**
     * 修改读者信息
     *
     * @param tbReader 读者信息
     * @return 结果
     */
    @Override
    public int updateTbReader(TbReader tbReader) {
        return tbReaderMapper.updateTbReader(tbReader);
    }

    /**
     * 批量删除读者信息
     *
     * @param rdIDs 需要删除的读者信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteTbReaderByRdIDs(Long[] rdIDs) {
        for (Long rdID : rdIDs) {
            // 1. 获取读者信息
            TbReader reader = tbReaderMapper.selectTbReaderByRdID(rdID);
            if (reader != null && reader.getRdCode() != null) {
                String userName = "rd" + String.valueOf(reader.getRdCode()).trim();

                // 2. 物理删除系统用户账号
                SysUser user = sysUserService.selectUserByUserName(userName);
                if (user != null) {
                    // 注意：这里要确保调用的是物理删除（DELETE FROM sys_user ...）
                    // 若依默认的 deleteUserByIds 可能是逻辑删除，建议直接用 userMapper
                    userMapper.deleteUserById(user.getUserId());
                }
            }
        }
        // 3. 物理删除读者记录
        // 确保 Mapper XML 里的 deleteTbReaderByRdIDs 是 <delete> 标签
        return tbReaderMapper.deleteTbReaderByRdIDs(rdIDs);
    }

    @Override
    public String getNextRdCode() {
        // ---------------------------------------------------------
        // 第一步：去 tb_reader 表查目前最大的号（别查 sys_user 了！）
        // ---------------------------------------------------------
        String maxCodeStr = tbReaderMapper.selectMaxRdCode();

        // 如果数据库是空的，默认从 0 开始；否则转成数字
        long nextId = (maxCodeStr == null ? 0 : Long.parseLong(maxCodeStr));

        // ---------------------------------------------------------
        // 第二步：死循环检测（双重保险，专治各种缓存和不同步）
        // ---------------------------------------------------------
        while (true) {
            nextId++; // 试探下一个号
            String tryCode = String.valueOf(nextId);

            // 去 tb_reader 表里实地查一下：这个号真的没人用吗？
            // (注意：这里直接复用查列表的方法，或者用 checkRdCodeUnique 都可以)
            TbReader search = new TbReader();
            search.setRdCode(tryCode);
            List<TbReader> list = tbReaderMapper.selectTbReaderList(search);

            // 如果查不到(list为空)，说明这个号是干净的，绝对安全！
            if (list == null || list.size() == 0) {
                return tryCode;
            }

            // 如果 list 不为空，说明 tryCode (比如 7) 已经被占用了。
            // 循环会继续执行，nextId++ 变成 8，再次检查... 直到找到空位为止。
        }
    }

    /**
     * 删除单个读者 (为了保险，把这个也加上逻辑，防止后台单独调用漏网)
     */
    @Override
    @Transactional
    public int deleteTbReaderByRdID(Long rdID) {
        // === 新增逻辑：先删关联的系统账号 ===
        TbReader reader = tbReaderMapper.selectTbReaderByRdID(rdID);
        if (reader != null && reader.getRdCode() != null) {
            SysUser user = sysUserService.selectUserByUserName(reader.getRdCode());
            if (user != null) {
                // 删除单个账号
                sysUserService.deleteUserById(user.getUserId());
            }
        }

        // === 原有逻辑：删除读者 ===
        return tbReaderMapper.deleteTbReaderByRdID(rdID);
    }

    /**
     * 恢复读者及账号逻辑
     * @param userId 系统用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recoverReader(Long userId) {
        // 1. 获取旧的账号信息（为了拿到旧的编号）
        SysUser oldUser = userMapper.selectUserById(userId);
        if (oldUser == null) {
            throw new RuntimeException("未找到待恢复的用户账号");
        }
        // 假设旧用户名是 "rd10"，截取得到 "10"
        String oldRdCode = oldUser.getUserName().substring(2);

        // 2. 获取新生成的编号（例如 "rd101"）
        String newRdCodeWithPrefix = getNextRdCode();
        String newNumberOnly = newRdCodeWithPrefix.substring(2);

        // 3. 更新系统用户信息 (sys_user)
        oldUser.setUserName(newRdCodeWithPrefix);
        oldUser.setNickName(newRdCodeWithPrefix);
        oldUser.setDelFlag("0");
        userMapper.updateUser(oldUser);

        // 4. 处理读者表记录 (tb_reader)
        // 【核心修正】：通过旧的 rdCode 去找读者记录
        TbReader reader = tbReaderMapper.selectTbReaderByCodeIncludeDeleted(oldRdCode);

        if (reader != null) {
            // 如果旧读者记录存在，更新编号和状态
            reader.setRdCode(newNumberOnly); // 这里的参数类型需与实体类一致
            reader.setRdName(newRdCodeWithPrefix);
            reader.setDelFlag("0");
            return tbReaderMapper.updateTbReader(reader);
        } else {
            // 如果没找到旧记录，则新建
            TbReader newReader = new TbReader();
            newReader.setRdCode(newNumberOnly);
            newReader.setRdName(newRdCodeWithPrefix);
            newReader.setDelFlag("0");
            return tbReaderMapper.insertTbReader(newReader);
        }
    }@Override
    @Transactional
    public int reissueCard(Long rdID) {
        // 1. 获取新证号
        String newRdCode = getNextRdCode();

        // 2. 查出读者信息
        TbReader reader = tbReaderMapper.selectTbReaderByRdID(rdID); // 注意这里是你刚才改对的方法名
        if (reader == null) {
            throw new ServiceException("该读者不存在");
        }

        // =========================================================
        // 📸 1. 先拍照：把旧名字存下来
        // =========================================================
        String oldUserName = "rd" + reader.getRdCode();

        // 🖨️【调试日志】看看这里打印的是 rd1 还是 rd8？
        System.out.println("--------------------------------------------------");
        System.out.println("【调试】准备查找旧账号，旧证号是：" + reader.getRdCode());
        System.out.println("【调试】拼出来的旧用户名是：" + oldUserName);

        // 3. 更新读者表
        reader.setRdCode(newRdCode);
        reader.setRdStatus("1");
        int rows = tbReaderMapper.updateTbReader(reader);

        // =========================================================
        // 🕵️‍♂️ 2. 去系统用户表找人
        // =========================================================
        com.ruoyi.common.core.domain.entity.SysUser sysUser = sysUserMapper.selectUserByUserName(oldUserName);

        if (sysUser != null) {
            System.out.println("【调试】成功找到用户！用户ID是：" + sysUser.getUserId());
            String newUserName = "rd" + newRdCode;
            sysUser.setUserName(newUserName);
            sysUserMapper.updateUser(sysUser);
            System.out.println("【调试】用户名已更新为：" + newUserName);
        } else {
            // 🛑 如果控制台打印这行，说明数据库里已经不一致了！
            System.err.println("【严重警告】在 sys_user 表里没找到账号：" + oldUserName);
            System.err.println("【原因猜测】可能之前的测试让 tb_reader 变了，但 sys_user 没变，导致现在对不上号。");
        }
        System.out.println("--------------------------------------------------");

        return rows;
    }



}