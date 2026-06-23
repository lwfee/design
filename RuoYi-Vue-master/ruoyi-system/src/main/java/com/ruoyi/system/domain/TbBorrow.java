package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 借阅记录对象 tb_borrow
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public class TbBorrow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 借阅流水号 */
    private Long BorrowID;

    @Excel(name = "读者编号")
    private String rdCode;

    /** 读者编号 */

    private Long rdID;



    /** 图书序号 */
    @Excel(name = "图书序号")
    private Long bkID;

    /** 续借次数 */
    @Excel(name = "续借次数")
    private Long ldContinueTimes;

    /** 借书日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "借书日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ldDateOut;

    /** 应还日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "应还日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ldDateRetPlan;

    /** 实际还书日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际还书日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ldDateRetAct;

    /** 超期天数 */
    @Excel(name = "超期天数")
    private Long ldOverDay;

    /** 超期金额 */
    @Excel(name = "超期金额")
    private BigDecimal ldOverMoney;

    /** 罚款金额 */
    @Excel(name = "罚款金额")
    private BigDecimal ldPunishMoney;

    /** 是否已还(0未还,1已还) */
    @Excel(name = "是否已还(0未还,1已还)")
    private Long IsHasReturn;

    /** 借书操作员 */
    @Excel(name = "借书操作员")
    private String OperatorBorrow;

    /** 还书操作员 */
    @Excel(name = "还书操作员")
    private String OperatorReturn;

    public void setBorrowID(Long BorrowID) 
    {
        this.BorrowID = BorrowID;
    }

    public Long getBorrowID() 
    {
        return BorrowID;
    }

    public void setRdCode(String rdCode) {
        this.rdCode = rdCode;
    }
    public String getRdCode() {
        return rdCode;
    }

    public void setRdID(Long rdID) 
    {
        this.rdID = rdID;
    }

    public Long getRdID() 
    {
        return rdID;
    }

    public void setBkID(Long bkID) 
    {
        this.bkID = bkID;
    }

    public Long getBkID() 
    {
        return bkID;
    }

    public void setLdContinueTimes(Long ldContinueTimes) 
    {
        this.ldContinueTimes = ldContinueTimes;
    }

    public Long getLdContinueTimes() 
    {
        return ldContinueTimes;
    }

    public void setLdDateOut(Date ldDateOut) 
    {
        this.ldDateOut = ldDateOut;
    }

    public Date getLdDateOut() 
    {
        return ldDateOut;
    }

    public void setLdDateRetPlan(Date ldDateRetPlan) 
    {
        this.ldDateRetPlan = ldDateRetPlan;
    }

    public Date getLdDateRetPlan() 
    {
        return ldDateRetPlan;
    }

    public void setLdDateRetAct(Date ldDateRetAct) 
    {
        this.ldDateRetAct = ldDateRetAct;
    }

    public Date getLdDateRetAct() 
    {
        return ldDateRetAct;
    }

    public void setLdOverDay(Long ldOverDay) 
    {
        this.ldOverDay = ldOverDay;
    }

    public Long getLdOverDay() 
    {
        return ldOverDay;
    }

    public void setLdOverMoney(BigDecimal ldOverMoney) 
    {
        this.ldOverMoney = ldOverMoney;
    }

    public BigDecimal getLdOverMoney() 
    {
        return ldOverMoney;
    }

    public void setLdPunishMoney(BigDecimal ldPunishMoney) 
    {
        this.ldPunishMoney = ldPunishMoney;
    }

    public BigDecimal getLdPunishMoney() 
    {
        return ldPunishMoney;
    }

    public void setIsHasReturn(Long IsHasReturn) 
    {
        this.IsHasReturn = IsHasReturn;
    }

    public Long getIsHasReturn() 
    {
        return IsHasReturn;
    }

    public void setOperatorBorrow(String OperatorBorrow) 
    {
        this.OperatorBorrow = OperatorBorrow;
    }

    public String getOperatorBorrow() 
    {
        return OperatorBorrow;
    }

    public void setOperatorReturn(String OperatorReturn) 
    {
        this.OperatorReturn = OperatorReturn;
    }

    public String getOperatorReturn() 
    {
        return OperatorReturn;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("BorrowID", getBorrowID())
            .append("rdID", getRdID())
            .append("bkID", getBkID())
            .append("ldContinueTimes", getLdContinueTimes())
            .append("ldDateOut", getLdDateOut())
            .append("ldDateRetPlan", getLdDateRetPlan())
            .append("ldDateRetAct", getLdDateRetAct())
            .append("ldOverDay", getLdOverDay())
            .append("ldOverMoney", getLdOverMoney())
            .append("ldPunishMoney", getLdPunishMoney())
            .append("IsHasReturn", getIsHasReturn())
            .append("OperatorBorrow", getOperatorBorrow())
            .append("OperatorReturn", getOperatorReturn())
            .toString();
    }
}
