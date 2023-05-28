package com.makro.mall.stat.pojo.dto;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/2/14
 */
@Data
public abstract class ClickRequest {


    @ApiModelProperty(value = "会员号")
    protected String memberNo;

    @ApiModelProperty(value = "MM编码")
    protected String mmCode;

    @ApiModelProperty(value = "门店编码")
    protected String storeCode;

    @ApiModelProperty(value = "渠道,email/sms/line/facebook/app")
    protected String channel;

    @ApiModelProperty(value = "ip", hidden = true)
    protected String ip;

    @ApiModelProperty(value = "创建时间，自动生成", hidden = true)
    protected Date createTime = new Date();

    @ApiModelProperty(value = "商品类型")
    private String goodsType;

    @ApiModelProperty(value = "页面类型")
    private String pageType;

    @ApiModelProperty(value = "bizId，用于跟踪")
    private String bizId;

    @ApiModelProperty(value = "ts，用于跟踪", hidden = true)
    private Long ts;

    private String uuid;
    /**
     * 生成bizId
     *
     * @return
     */
    public String generateBizId() {
        if (StrUtil.isEmpty(bizId)) {
            bizId = UUID.randomUUID().toString();
        }
        return bizId;
    }

}
