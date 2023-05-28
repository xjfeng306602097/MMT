package com.makro.mall.file.pojo.vo;

import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/6/28 element文件夹
 */
@Data
public class ElementTreeVO extends BaseEntity {

    private Long id;

    private String name;

    @ApiModelProperty("0为文件夹")
    private String type;

    @ApiModelProperty("0为文件夹")
    private Long elementId;

    @ApiModelProperty("element对象")
    private MmElementVO elementVO;

    private String remark;
    /**
     * 0为根节点
     */
    @ApiModelProperty("父id,0为根节点")
    @Min(0)
    private Long parentId;

    private Boolean isPublic;
}
