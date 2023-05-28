package com.makro.mall.stat.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ChannelvisitorsDTO {
    private String mmCode;
    private String channel;
    /**
     * mm页面访问数
     */
    private Long value;
    private Long uv;
    private Date date;
}
