package com.makro.mall.stat.pojo.dto;

import lombok.Data;

@Data
public class AverageVisitorVisitsPvMvDTO {
    /**
     *
     */
    private String mmCode;

    /**
     * 除了app都是H5
     */
    private String channel;

    private Double pv;

    private Double mv;
    /**
     *
     */
    private Double pvDivideMv;
}
