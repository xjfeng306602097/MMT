package com.makro.mall.admin.pojo.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.makro.mall.admin.pojo.entity.MmPublishJobSmsTask;
import com.makro.mall.common.enums.MessageTaskEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author jincheng
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MmPublishJobSmsTaskV2DTO extends BasePublishJobDTO {

    @NotNull
    @ApiModelProperty(value = "发布任务ID")
    private Long jobId;
    @NotNull
    @ApiModelProperty(value = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime workTime;
    @NotNull
    @ApiModelProperty(value = "邮件主题")
    private String msg;
    @ApiModelProperty(value = "模板")
    private String template;
    @ApiModelProperty(value = "页码")
    private Integer pageNo;
    @ApiModelProperty(value = "预览图")
    private String reviewUrl;
    @ApiModelProperty(value = "门店")
    private String storeCode;
    @ApiModelProperty(hidden = true)
    private String mmCode;
    @ApiModelProperty(hidden = true)
    private String publishUrl;
    @ApiModelProperty(value = "客户ID集合")
    private Set<Long> mmCustomerId;

    public static MmPublishJobSmsTask toNotSent(MmPublishJobSmsTaskV2DTO publishJobTaskDTO) {
        MmPublishJobSmsTask publishJobTask = new MmPublishJobSmsTask();
        BeanUtil.copyProperties(publishJobTaskDTO, publishJobTask);
        publishJobTask.setMmCustomerId(StrUtil.join(",", publishJobTaskDTO.getMmCustomerId()));
        publishJobTask.setMmPublishJobId(publishJobTaskDTO.getJobId());
        if (ObjectUtil.isNotNull(publishJobTaskDTO.getExceptList())) {
            publishJobTask.setBlacklist(publishJobTaskDTO.getExceptList().getCustomersS3Url());
        }
        if (ObjectUtil.isNotNull(publishJobTaskDTO.getSendList())) {
            publishJobTask.setWhitelist(publishJobTaskDTO.getSendList().getCustomersS3Url());
        }
        publishJobTask.setStatus(MessageTaskEnum.NOT_SENT.getStatus());
        return publishJobTask;
    }

}
