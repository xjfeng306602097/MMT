package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName MM_PUBLISH_JOB_SMS_TASK
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_PUBLISH_JOB_SMS_TASK")
@Data
public class MmPublishJobSmsTask extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId
    private Long id;
    /**
     *
     */
    private Long mmPublishJobId;
    /**
     *
     */
    @NotNull
    @ApiModelProperty(value = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime workTime;
    /**
     *
     */
    private Long status;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    /**
     *
     */
    private String msg;
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     *
     */
    private String mmCustomerId;
    private String publishUrl;
    @ApiModelProperty(value = "会员号黑名单")
    private String blacklist;
    @ApiModelProperty(value = "会员号白名单")
    private String whitelist;
    private String mmCode;
}
