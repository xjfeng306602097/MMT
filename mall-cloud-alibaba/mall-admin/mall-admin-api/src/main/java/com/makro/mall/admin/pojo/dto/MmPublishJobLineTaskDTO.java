package com.makro.mall.admin.pojo.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
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
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class MmPublishJobLineTaskDTO {

    @NotNull
    @ApiModelProperty(value = "发布任务ID")
    private Long jobId;

    @NotNull
    @ApiModelProperty(value = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime workTime;

    @ApiModelProperty(value = "对应客户ID,群发可不填")
    private Set<Long> mmCustomerId;

    @ApiModelProperty(value = "segmentId")
    private Set<Long> segmentId;

    @NotNull
    @ApiModelProperty(value = "line主题")
    private String subject;

    @ApiModelProperty(value = "line封面图,不填取mm缩略图")
    private String coverUrl;

    @ApiModelProperty(value = "line消息是否群发")
    private Boolean isBroadcast;
    @ApiModelProperty(value = "会员号黑名单")
    private String blacklist;
    @ApiModelProperty(value = "会员号白名单")
    private String whitelist;
    @ApiModelProperty(value = "mmCode", hidden = true)
    private String mmCode;
    @ApiModelProperty(value = "模板")
    private String template;
    @ApiModelProperty(value = "页码")
    private Integer pageNo;
    @ApiModelProperty(value = "门店")
    private String storeCode;


    public static MmPublishJobLineTask toEntity(MmPublishJobLineTaskDTO publishJobTaskDTO) {
        MmPublishJobLineTask publishJobTask = new MmPublishJobLineTask();
        BeanUtil.copyProperties(publishJobTaskDTO, publishJobTask);
        publishJobTask.setMmPublishJobId(publishJobTaskDTO.getJobId());
        publishJobTask.setId(IdUtil.fastSimpleUUID());
        publishJobTask.setMmCustomerId(StrUtil.join(",", publishJobTaskDTO.getMmCustomerId()));
        publishJobTask.setBlacklist(publishJobTaskDTO.getBlacklist());
        publishJobTask.setWhitelist(publishJobTaskDTO.getWhitelist());
        publishJobTask.setMmCode(publishJobTaskDTO.getMmCode());
        publishJobTask.setTemplate(publishJobTaskDTO.getTemplate());
        publishJobTask.setPageNo(publishJobTaskDTO.getPageNo());
        publishJobTask.setStatus(MessageTaskEnum.NOT_SENT.getStatus());
        return publishJobTask;
    }


}
