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
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName MM_PUBLISH_JOB_EMAIL_TASK
 */
@TableName(value = "MM_PUBLISH_JOB_EMAIL_TASK")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MmPublishJobEmailTask extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId
    private String id;
    /**
     * 发布任务ID
     */
    private Long mmPublishJobId;
    /**
     * 执行时间
     */
    @ApiModelProperty(value = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime workTime;
    /**
     * 0-发布失败,1-未发布,2-发布中,3-发布成功,4-发布部分成功
     */
    @NonNull
    private Long status;
    /**
     * 客户id
     */
    private String mmCustomerId;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件模板
     */
    private String template;
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 预览图
     */
    private String reviewUrl;
    private String publishUrl;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    @ApiModelProperty(value = "会员号黑名单 文件地址")
    private String blacklist;
    @ApiModelProperty(value = "会员号白名单 文件地址")
    private String whitelist;
    private String mmCode;
}
