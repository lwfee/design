package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图书信息对象 tb_book
 * 
 * @author ruoyi
 * @date 2025-12-14
 */
public class TbBook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图书序号 */
    private Long bkID;

    /** 图书条码号 */
    @Excel(name = "图书条码号")
    private String bkCode;

    /** 书名 */
    @Excel(name = "书名")
    private String bkName;

    /** 作者 */
    @Excel(name = "作者")
    private String bkAuthor;

    /** 出版社 */
    @Excel(name = "出版社")
    private String bkPress;

    /** 出版日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出版日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bkDatePress;

    /** ISBN书号 */
    @Excel(name = "ISBN书号")
    private String bkISBN;

    /** 分类号 */
    @Excel(name = "分类号")
    private String bkCatalog;

    /** 语言 */
    @Excel(name = "语言")
    private String bkLanguage;

    /** 页数 */
    @Excel(name = "页数")
    private Long bkPages;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal bkPrice;

    /** 入馆日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入馆日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bkDateIn;

    /** 内容简介 */
    @Excel(name = "内容简介")
    private String bkBrief;

    /** 图书状态 */
    @Excel(name = "图书状态")
    private String bkStatus;

    public void setBkID(Long bkID) 
    {
        this.bkID = bkID;
    }

    public Long getBkID() 
    {
        return bkID;
    }

    public void setBkCode(String bkCode) 
    {
        this.bkCode = bkCode;
    }

    public String getBkCode() 
    {
        return bkCode;
    }

    public void setBkName(String bkName) 
    {
        this.bkName = bkName;
    }

    public String getBkName() 
    {
        return bkName;
    }

    public void setBkAuthor(String bkAuthor) 
    {
        this.bkAuthor = bkAuthor;
    }

    public String getBkAuthor() 
    {
        return bkAuthor;
    }

    public void setBkPress(String bkPress) 
    {
        this.bkPress = bkPress;
    }

    public String getBkPress() 
    {
        return bkPress;
    }

    public void setBkDatePress(Date bkDatePress) 
    {
        this.bkDatePress = bkDatePress;
    }

    public Date getBkDatePress() 
    {
        return bkDatePress;
    }

    public void setBkISBN(String bkISBN) 
    {
        this.bkISBN = bkISBN;
    }

    public String getBkISBN() 
    {
        return bkISBN;
    }

    public void setBkCatalog(String bkCatalog) 
    {
        this.bkCatalog = bkCatalog;
    }

    public String getBkCatalog() 
    {
        return bkCatalog;
    }

    public void setBkLanguage(String bkLanguage) 
    {
        this.bkLanguage = bkLanguage;
    }

    public String getBkLanguage() 
    {
        return bkLanguage;
    }

    public void setBkPages(Long bkPages) 
    {
        this.bkPages = bkPages;
    }

    public Long getBkPages() 
    {
        return bkPages;
    }

    public void setBkPrice(BigDecimal bkPrice) 
    {
        this.bkPrice = bkPrice;
    }

    public BigDecimal getBkPrice() 
    {
        return bkPrice;
    }

    public void setBkDateIn(Date bkDateIn) 
    {
        this.bkDateIn = bkDateIn;
    }

    public Date getBkDateIn() 
    {
        return bkDateIn;
    }

    public void setBkBrief(String bkBrief) 
    {
        this.bkBrief = bkBrief;
    }

    public String getBkBrief() 
    {
        return bkBrief;
    }

    public void setBkStatus(String bkStatus) 
    {
        this.bkStatus = bkStatus;
    }

    public String getBkStatus() 
    {
        return bkStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bkID", getBkID())
            .append("bkCode", getBkCode())
            .append("bkName", getBkName())
            .append("bkAuthor", getBkAuthor())
            .append("bkPress", getBkPress())
            .append("bkDatePress", getBkDatePress())
            .append("bkISBN", getBkISBN())
            .append("bkCatalog", getBkCatalog())
            .append("bkLanguage", getBkLanguage())
            .append("bkPages", getBkPages())
            .append("bkPrice", getBkPrice())
            .append("bkDateIn", getBkDateIn())
            .append("bkBrief", getBkBrief())
            .append("bkStatus", getBkStatus())
            .toString();
    }
}
