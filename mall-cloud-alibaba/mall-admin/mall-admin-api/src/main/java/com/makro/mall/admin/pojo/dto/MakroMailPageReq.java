package com.makro.mall.admin.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/18
 */
@Data
public class MakroMailPageReq {

    @ApiModelProperty(value = "店铺code")
    private String storeCode;

    @ApiModelProperty(value = "会员类型")
    private String memberType;

    @ApiModelProperty(value = "会员号")
    private String memberCode;

    @ApiModelProperty(value = "模糊查询 appTitle")
    private String appTitle;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MakroMailPageReq req = (MakroMailPageReq) o;
        return storeCode.equals(req.storeCode) && memberType.equals(req.memberType) && memberCode.equals(req.memberCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeCode, memberType, memberCode);
    }
}
