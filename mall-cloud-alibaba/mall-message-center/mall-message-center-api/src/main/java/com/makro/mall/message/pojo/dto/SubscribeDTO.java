package com.makro.mall.message.pojo.dto;

import cn.hutool.crypto.digest.MD5;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/1/13
 */
@Data
@ApiModel("订阅实体")
public class SubscribeDTO {

    @NotNull
    @ApiModelProperty(value = "邮箱地址,从路径上获取")
    private String address;

    @NotNull
    @ApiModelProperty(value = "签名,从路径上获取")
    private String d;

    @ApiModelProperty(value = "取消订阅原因,取消订阅时传")
    private String reason;

    @ApiModelProperty(value = "取消订阅备注,取消订阅时传")
    private String remark;

    public boolean checkAddress(String salt) {
        String str = MD5.create().digestHex(address + salt);
        return str.equals(d);
    }

}
