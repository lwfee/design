package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 读者类别对象 tb_readertype
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public class TbReadertype extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 读者类别编号 */
    private Long rdType;

    /** 类别名称 */
    @Excel(name = "类别名称")
    private String rdTypeName;

    /** 可借书数量 */
    @Excel(name = "可借书数量")
    private Long CanLendQty;

    /** 可借书天数 */
    @Excel(name = "可借书天数")
    private Long CanLendDay;

    /** 可续借次数 */
    @Excel(name = "可续借次数")
    private Long CanContinueTimes;

    /** 罚款率(元/天) */
    @Excel(name = "罚款率(元/天)")
    private BigDecimal PunishRate;

    /** 证书有效期(年) */
    @Excel(name = "证书有效期(年)")
    private Long DateValid;

    public void setRdType(Long rdType) 
    {
        this.rdType = rdType;
    }

    public Long getRdType() 
    {
        return rdType;
    }

    public void setRdTypeName(String rdTypeName) 
    {
        this.rdTypeName = rdTypeName;
    }

    public String getRdTypeName() 
    {
        return rdTypeName;
    }

    public void setCanLendQty(Long CanLendQty) 
    {
        this.CanLendQty = CanLendQty;
    }

    public Long getCanLendQty() 
    {
        return CanLendQty;
    }

    public void setCanLendDay(Long CanLendDay) 
    {
        this.CanLendDay = CanLendDay;
    }

    public Long getCanLendDay() 
    {
        return CanLendDay;
    }

    public void setCanContinueTimes(Long CanContinueTimes) 
    {
        this.CanContinueTimes = CanContinueTimes;
    }

    public Long getCanContinueTimes() 
    {
        return CanContinueTimes;
    }

    public void setPunishRate(BigDecimal PunishRate) 
    {
        this.PunishRate = PunishRate;
    }

    public BigDecimal getPunishRate() 
    {
        return PunishRate;
    }

    public void setDateValid(Long DateValid) 
    {
        this.DateValid = DateValid;
    }

    public Long getDateValid() 
    {
        return DateValid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("rdType", getRdType())
            .append("rdTypeName", getRdTypeName())
            .append("CanLendQty", getCanLendQty())
            .append("CanLendDay", getCanLendDay())
            .append("CanContinueTimes", getCanContinueTimes())
            .append("PunishRate", getPunishRate())
            .append("DateValid", getDateValid())
            .toString();
    }
}
