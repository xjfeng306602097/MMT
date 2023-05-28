package com.makro.mall.stat.pojo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppUvRecord extends AbstractLogRecord{
    /**
     * 是否是新的请求
     */
    private Integer isNew;
    @ApiModelProperty(value = "万客隆会员号")
    protected String memberNo;
    @ApiModelProperty(value = "创建时间，自动生成", hidden = true)
    protected Date createTime = new Date();

}
