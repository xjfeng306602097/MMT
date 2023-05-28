package com.makro.mall.admin.pojo.dto;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.makro.mall.admin.pojo.entity.MmPublishJobLineTask;
import com.makro.mall.common.enums.MessageTaskEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author jincheng
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class MmPublishJobLineTaskV2DTO extends BasePublishJobDTO {

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
    private String subject;
    @ApiModelProperty(value = "模板")
    private String template;
    @ApiModelProperty(value = "页码")
    private Integer pageNo;
    @ApiModelProperty(value = "预览图")
    private String coverUrl;
    @ApiModelProperty(value = "门店")
    private String storeCode;
    @ApiModelProperty(hidden = true)
    private String mmCode;
    @ApiModelProperty(hidden = true)
    private String publishUrl;
    @ApiModelProperty(value = "客户ID集合")
    private Set<Long> mmCustomerId;
    @ApiModelProperty(value = "是否群发")
    private Boolean isBroadcast;
    @ApiModelProperty(value = "模板号")
    private Integer templateNo;

    public static MmPublishJobLineTask toNotSent(MmPublishJobLineTaskV2DTO publishJobTaskDTO) {
        MmPublishJobLineTask publishJobTask = new MmPublishJobLineTask();
        BeanUtil.copyProperties(publishJobTaskDTO, publishJobTask);
        publishJobTask.setMmPublishJobId(publishJobTaskDTO.getJobId());
        publishJobTask.setId(IdUtil.fastSimpleUUID());
        publishJobTask.setMmCustomerId(StrUtil.join(",", publishJobTaskDTO.getMmCustomerId()));
        if (ObjectUtil.isNotNull(publishJobTaskDTO.getExceptList())) {
            publishJobTask.setBlacklist(publishJobTaskDTO.getExceptList().getCustomersS3Url());
        }
        if (ObjectUtil.isNotNull(publishJobTaskDTO.getSendList())) {
            publishJobTask.setWhitelist(publishJobTaskDTO.getSendList().getCustomersS3Url());
        }
        publishJobTask.setMmCode(publishJobTaskDTO.getMmCode());
        publishJobTask.setTemplate(publishJobTaskDTO.getTemplate());
        publishJobTask.setPageNo(publishJobTaskDTO.getPageNo());
        publishJobTask.setStatus(MessageTaskEnum.NOT_SENT.getStatus());
        return publishJobTask;
    }
}