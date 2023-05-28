package com.makro.mall.file.pojo.vo;

import com.makro.mall.file.pojo.entity.MmElementTree;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ElementTreeListReqVO extends MmElementTree {

    @ApiModelProperty("根目录为0")
    @NotNull
    private Long id;
    @NotBlank
    private String elementType;
}
