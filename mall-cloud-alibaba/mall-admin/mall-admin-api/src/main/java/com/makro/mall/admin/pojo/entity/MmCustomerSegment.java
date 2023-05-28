package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户关联SEGMENT表
 *
 * @TableName MM_CUSTOMER_SEGMENT
 */
@TableName(value = "MM_CUSTOMER_SEGMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MmCustomerSegment implements Serializable {
    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * SEGMENT ID
     */
    private Long segmentId;

}