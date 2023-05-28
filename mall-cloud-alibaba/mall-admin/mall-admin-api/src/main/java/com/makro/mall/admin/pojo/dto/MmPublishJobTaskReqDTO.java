package com.makro.mall.admin.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Slf4j
public class MmPublishJobTaskReqDTO {
    @NotNull
    @ApiModelProperty(value = "开始执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startWorkTime;
    @NotNull
    @ApiModelProperty(value = "结束执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endWorkTime;

    @ApiModelProperty(value = "0-发布失败,1-未发布,2-发布中,3-发布成功")
    private Long status;

    @ApiModelProperty(value = "邮件主题")
    private String subject;

    @ApiModelProperty(value = "门店")
    private String storeCode;

    @ApiModelProperty(value = "渠道 sms/line/email")
    private String channel;

    @ApiModelProperty(value = "mmCode")
    private String mmCode;

    @ApiModelProperty(value = "JobId")
    private String jobId;

}
