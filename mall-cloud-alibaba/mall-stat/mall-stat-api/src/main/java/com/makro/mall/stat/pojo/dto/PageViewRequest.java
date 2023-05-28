package com.makro.mall.stat.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/2/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PageViewRequest extends ClickRequest {

    @ApiModelProperty(value = "页码")
    private String pageNo;

    @ApiModelProperty(value = "url地址")
    private String pageUrl;

    @ApiModelProperty(value = "页面类型")
    private String pageType;

}
