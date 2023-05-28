package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName average_visitor_visits
 */
@TableName(value = "average_visitor_visits")
@Data
public class AverageVisitorVisits implements Serializable {
    /**
     *
     */
    private String mmCode;

    /**
     * 除了app都是H5
     */
    private String channel;

    /**
     *
     */
    private Double pvDivideMv;

    /**
     *
     */
    private Date date;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}