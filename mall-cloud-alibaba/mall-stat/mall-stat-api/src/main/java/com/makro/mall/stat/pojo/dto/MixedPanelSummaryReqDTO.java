package com.makro.mall.stat.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MixedPanelSummaryReqDTO {
    String mmCode;
    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    Date startTime;
    String step1;
    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    Date endTime;
    List<String> pageNos;
    @ApiModelProperty(value = "Item Click 等")
    List<String> dataType;
    List<String> step;
    List<String> memberTypes;
    List<String> channels;
    @ApiParam("1-手机 0-不是手机")
    List<String> mobile;
}
