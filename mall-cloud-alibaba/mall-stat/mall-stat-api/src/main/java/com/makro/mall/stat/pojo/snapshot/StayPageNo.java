package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jincheng
 * @TableName stay_mm_code
 */
@TableName(value = "stay_page_no")
@Data
public class StayPageNo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String mmCode;
    /**
     * 停留时间，ms为单位
     */
    private Long stayTime;
    /**
     * 停留时间，ms为单位
     */
    private String pageNo;
    /**
     * 停留时间，ms为单位
     */
    private Date date;


}