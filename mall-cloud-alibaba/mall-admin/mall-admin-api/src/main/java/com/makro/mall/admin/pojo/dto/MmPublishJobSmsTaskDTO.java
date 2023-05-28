package com.makro.mall.admin.pojo.dto;

import cn.hutool.core.bean.BeanUtil;
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
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MmPublishJobSmsTaskDTO {

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
    @ApiModelProperty(value = "对应客户ID")
    private List<Long> mmCustomerId;

    @NotNull
    @ApiModelProperty(value = "短信内容")
    private String msg;
    @ApiModelProperty(value = "会员号黑名单")
    private String blacklist;
    @ApiModelProperty(value = "会员号白名单")
    private String whitelist;
    @ApiModelProperty(value = "mmCode", hidden = true)
    private String mmCode;
    @ApiModelProperty(value = "页码")
    private Integer pageNo;
    @ApiModelProperty(value = "门店")
    private String storeCode;
    @ApiModelProperty(hidden = true)
    private String publishUrl;

    public static MmPublishJobSmsTask toEntity(MmPublishJobSmsTaskDTO publishJobTaskDTO) {
        MmPublishJobSmsTask publishJobTask = new MmPublishJobSmsTask();
        BeanUtil.copyProperties(publishJobTaskDTO, publishJobTask);
        publishJobTask.setMmCustomerId(StrUtil.join(",", publishJobTaskDTO.getMmCustomerId()));
        publishJobTask.setMmPublishJobId(publishJobTaskDTO.getJobId());
        publishJobTask.setBlacklist(publishJobTaskDTO.getBlacklist());
        publishJobTask.setWhitelist(publishJobTaskDTO.getWhitelist());
        publishJobTask.setStatus(MessageTaskEnum.NOT_SENT.getStatus());
        return publishJobTask;
    }
}
