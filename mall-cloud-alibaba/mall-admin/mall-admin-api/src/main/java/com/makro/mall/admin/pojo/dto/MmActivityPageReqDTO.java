package com.makro.mall.admin.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 活动分页查询实体
 * @date 2021/10/29
 */
@Data
public class MmActivityPageReqDTO {

    private String title;

    private List<String> storeCode;

    private String type;

    @ApiModelProperty("支持多个status传入")
    private List<Integer> statusList;

    private Integer status;

    private Integer list;

    private Date startTime;

    private Date endTime;

    private Long page;

    private Long limit;

    private String code;

    @ApiModelProperty(value = "是否快速创建模板,0-否，1-是，默认0")
    private Boolean isQuickCreate;

    @ApiModelProperty(value = "商品编码")
    private String itemCode;

    @ApiModelProperty(value = "是否查询模板信息 0-不查")
    private Integer templateInfo = 0;

}
