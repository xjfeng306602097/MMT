package com.makro.mall.stat.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class VisitorDataVO {
    @ApiModelProperty("访问时间")
    private Date createTime;
    @ApiModelProperty("访问页面")
    private String url;
    @ApiModelProperty("会员账号")
    private String memberNo;
    @ApiModelProperty("会员类型")
    private String memberType;
    @ApiModelProperty("IP地址")
    private String ip;
    @ApiModelProperty("mmCode")
    private String mmCode;
    @ApiModelProperty("storeCode")
    private String storeCode;
    @ApiModelProperty("设备类型 IOS/Android/Windows/Mac/Linux/....")
    private String platform;
    @ApiModelProperty("设备类型详情")
    private String os;
    @ApiModelProperty("进入渠道 email/sms/line/facebook/app")
    private String channel;
    @ApiModelProperty("打开方式 pdf/h5")
    private String publishType;
}
