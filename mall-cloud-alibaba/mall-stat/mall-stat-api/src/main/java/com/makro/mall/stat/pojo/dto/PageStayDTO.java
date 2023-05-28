package com.makro.mall.stat.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/10/10
 */
@Data
public class PageStayDTO {

    private String pageNo;

    private Long visits;

    private Long visitors;

    private Long newVisitors;

    private BigDecimal averageStayTime;

    private Long stayTime;

    private Long bounceRateCounts;

    private BigDecimal bounceRate;


}
