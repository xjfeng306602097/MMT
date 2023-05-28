package com.makro.mall.stat.pojo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/27
 */
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUvRecord extends AbstractLogRecord {

    @ApiModelProperty("是否是新的请求")
    private Integer isNew;

    @ApiModelProperty(value = "万客隆会员号")
    protected String memberNo;

}
