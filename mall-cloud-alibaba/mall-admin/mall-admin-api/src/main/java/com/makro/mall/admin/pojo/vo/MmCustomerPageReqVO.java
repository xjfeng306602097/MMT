package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmCustomer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * 功能描述: 客户VO
 * 客户资料上传&Segment维护
 *
 * @Author: 卢嘉俊
 * @Date: 2022/5/12
 */
@Data
public class MmCustomerPageReqVO extends MmCustomer {

    @ApiModelProperty("segment id列表")
    Set<Long> segments;

    @ApiModelProperty("memberType id列表")
    Set<String> memberTypeIds;

}
