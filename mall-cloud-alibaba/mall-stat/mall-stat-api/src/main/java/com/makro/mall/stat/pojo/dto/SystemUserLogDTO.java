package com.makro.mall.stat.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName system_user_log
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemUserLogDTO implements Serializable {

    @ApiModelProperty("page、button")
    private String type;

    @ApiModelProperty("系统用户Id")
    private String userId;

    @ApiModelProperty("系统用户名称")
    private String userName;

    @ApiModelProperty("系统用户ip")
    private String userIp;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("开始 创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTimeStart;

    @ApiModelProperty("结束 创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTimeEnd;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
