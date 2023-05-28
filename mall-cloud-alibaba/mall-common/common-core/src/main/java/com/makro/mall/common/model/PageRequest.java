package com.makro.mall.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description 分页查询请求
 * @date 2021/10/9
 */
@ApiModel("分页查询请求实体")
@Data
public class PageRequest {
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final Integer DEFAULT_PAGE_NUM = 1;

    @ApiModelProperty(
            value = "页码，一般都是从1开始的，但是有一些历史遗留项目是从0开始的，不确定的话就问下开发",
            example = "1"
    )
    private Integer pageNo;
    @ApiModelProperty(
            value = "每页条数",
            example = "10"
    )
    private Integer pageSize;

    public PageRequest() {
        this.pageNo = DEFAULT_PAGE_NUM;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PageRequest(final Integer pageNo, final Integer pageSize) {
        this.pageNo = DEFAULT_PAGE_NUM;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
