package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @author jincheng
 * @TableName MM_PUBLISH_JOB_FILE
 */
@TableName(value ="MM_PUBLISH_JOB_FILE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MmPublishJobFile implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 发布任务ID
     */
    private String mmPublishJobId;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 渠道
     */
    private String channel;

    @TableField(fill = FieldFill.INSERT)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}