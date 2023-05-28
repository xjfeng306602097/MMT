package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * 客户表
 *
 * @TableName MM_CUSTOMER
 */
@TableName(value = "MM_CUSTOMER")
@Data
@Accessors(chain = true)
public class MmCustomer extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 客户名称
     */
    private String name;
    /**
     * 客户手机号
     */
    private String phone;
    /**
     * 客户邮箱
     */
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;
    /**
     * 逻辑删除标识：0-未删除；1-已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Long deleted;
    /**
     * 客户编码
     */
    private String customerCode;
    private String mmCode;
    private String lineId;
    /**
     * line主体
     */
    private String lineBotChannelToken;

    public static Set<String> getReturnPhone(String phone) {
        return Set.of(phone, "0" + phone.substring(2), phone.substring(2));
    }

    public static String getValidPhone(String phone, String prefix) {
        return phone.startsWith(prefix) ? phone : (phone.startsWith("0") ? prefix + phone.substring(1) : prefix + phone);
    }
}
