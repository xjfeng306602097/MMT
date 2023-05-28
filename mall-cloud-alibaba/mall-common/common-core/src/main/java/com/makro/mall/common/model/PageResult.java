package com.makro.mall.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 分页数据封装类
 * @date 2021/10/9
 */
@ApiModel(
        value = "分页数据封装类",
        description = "定义分页数据响应的基本字段"
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageResult<T> {

    @ApiModelProperty(value = "页码")
    private Integer pageNum;
    @ApiModelProperty(value = "页数")
    private Integer pageSize;
    @ApiModelProperty(value = "总页数")
    private Integer totalPage;
    @ApiModelProperty(value = "总数")
    private Long total;
    @ApiModelProperty(value = "列表数据")
    private List<T> list;

    /**
     * 将SpringData分页后的list转为分页信息
     */
    public static <T> PageResult<T> restPage(Page<T> pageInfo) {
        PageResult<T> pageResult = new PageResult<T>();
        pageResult.setTotalPage(pageInfo.getTotalPages());
        pageResult.setPageNum(pageInfo.getNumber());
        pageResult.setPageSize(pageInfo.getSize());
        pageResult.setTotal(pageInfo.getTotalElements());
        pageResult.setList(pageInfo.getContent());
        return pageResult;
    }

}
