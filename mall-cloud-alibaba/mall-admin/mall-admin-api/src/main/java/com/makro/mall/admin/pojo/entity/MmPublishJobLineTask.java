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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName MM_PUBLISH_JOB_LINE_TASK
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_PUBLISH_JOB_LINE_TASK")
@Data
public class MmPublishJobLineTask extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId
    private String id;
    /**
     *
     */
    private Long mmPublishJobId;
    /**
     *
     */
    @ApiModelProperty(value = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime workTime;
    /**
     * 0-发布失败,1-未发布,2-发布中,3-发布成功,4-发布部分成功
     * 上面已过时 详情看MessageTaskEnum
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
    private String subject;
    /**
     *
     */
    private String mmCustomerId;
    /**
     * 邮件模板
     */
    private String template;
    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * line封面图,不填取mm缩略图
     */
    private String coverUrl;
    @ApiModelProperty(value = "会员号黑名单")
    private String blacklist;
    @ApiModelProperty(value = "会员号白名单")
    private String whitelist;
    private String mmCode;

}
