package com.makro.mall.template.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/1/5
 */
@Data
@ApiModel("发布请求")
public class MmPublishReq {

    @ApiModelProperty(value = "mmCode")
    private String code;

    @ApiModelProperty(value = "预览图地址")
    private String previewUrl;

    @ApiModelProperty(value = "h5路径")
    private String publishUrl;

}
