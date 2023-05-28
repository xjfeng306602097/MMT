package com.makro.mall.admin.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jincheng
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MmActivityAppPageRepVO {
    @ApiModelProperty("appUrl")
    private String url;
    @ApiModelProperty(value = "预览图链接")
    private String previewUrl;
    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;
    private String mmCode;
    @ApiModelProperty(value = "名称")
    private String title;
    @ApiModelProperty(value = "类型，新增必传")
    private String type;
    @ApiModelProperty(value = "店铺code,新增必传")
    private String storeCode;

}
