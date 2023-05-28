package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName visitor_clicks_on_product
 */
@TableName(value = "visitor_clicks_on_product")
@Data
public class VisitorClicksOnProduct implements Serializable {
    /**
     *
     */
    private String mmCode;

    /**
     *
     */
    private String member;

    /**
     *
     */
    private Long productClick;

    /**
     *
     */
    private Date date;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}