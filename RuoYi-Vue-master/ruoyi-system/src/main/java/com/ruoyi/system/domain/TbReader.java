package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 读者信息对象 tb_reader
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public class TbReader extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 读者编号 */
    private Long rdID;

    private String delFlag;

    @Excel(name = "读者证号") // 加上这个，导出Excel时也会有它
    private String rdCode;

    /** 读者姓名 */
    @Excel(name = "读者姓名")
    private String rdName;

    /** 性别 */
    @Excel(name = "性别")
    private String rdSex;

    /** 读者类别 */
    @Excel(name = "读者类别")
    private Long rdType;

    /** 单位名称 */
    @Excel(name = "单位名称")
    private String rdDept;

    /** 电话号码 */
    @Excel(name = "电话号码")
    private String rdPhone;

    /** 电子邮箱 */
    @Excel(name = "电子邮箱")
    private String rdEmail;

    /** 办证日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "办证日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date rdDateReg;

    /** 读者照片 */
    @Excel(name = "读者照片")
    private String rdPhoto;

    /** 证件状态 */
    @Excel(name = "证件状态")
    private String rdStatus;

    /** 已借书数量 */
    @Excel(name = "已借书数量")
    private Long rdBorrowQty;

    public void setRdID(Long rdID) 
    {
        this.rdID = rdID;
    }

    public Long getRdID() 
    {
        return rdID;
    }

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public String getDelFlag() { return delFlag; }

    public void setRdCode(String rdCode) { this.rdCode = rdCode; }

    public String getRdCode() { return rdCode; }

    public void setRdName(String rdName) 
    {
        this.rdName = rdName;
    }

    public String getRdName() 
    {
        return rdName;
    }

    public void setRdSex(String rdSex) 
    {
        this.rdSex = rdSex;
    }

    public String getRdSex() 
    {
        return rdSex;
    }

    public void setRdType(Long rdType) 
    {
        this.rdType = rdType;
    }

    public Long getRdType() 
    {
        return rdType;
    }

    public void setRdDept(String rdDept) 
    {
        this.rdDept = rdDept;
    }

    public String getRdDept() 
    {
        return rdDept;
    }

    public void setRdPhone(String rdPhone) 
    {
        this.rdPhone = rdPhone;
    }

    public String getRdPhone() 
    {
        return rdPhone;
    }

    public void setRdEmail(String rdEmail) 
    {
        this.rdEmail = rdEmail;
    }

    public String getRdEmail() 
    {
        return rdEmail;
    }

    public void setRdDateReg(Date rdDateReg) 
    {
        this.rdDateReg = rdDateReg;
    }

    public Date getRdDateReg() 
    {
        return rdDateReg;
    }

    public void setRdPhoto(String rdPhoto) 
    {
        this.rdPhoto = rdPhoto;
    }

    public String getRdPhoto() 
    {
        return rdPhoto;
    }

    public void setRdStatus(String rdStatus) 
    {
        this.rdStatus = rdStatus;
    }

    public String getRdStatus() 
    {
        return rdStatus;
    }

    public void setRdBorrowQty(Long rdBorrowQty) 
    {
        this.rdBorrowQty = rdBorrowQty;
    }

    public Long getRdBorrowQty() 
    {
        return rdBorrowQty;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("rdID", getRdID())
            .append("rdName", getRdName())
            .append("rdSex", getRdSex())
            .append("rdType", getRdType())
            .append("rdDept", getRdDept())
            .append("rdPhone", getRdPhone())
            .append("rdEmail", getRdEmail())
            .append("rdDateReg", getRdDateReg())
            .append("rdPhoto", getRdPhoto())
            .append("rdStatus", getRdStatus())
            .append("rdBorrowQty", getRdBorrowQty())
            .toString();
    }
}
