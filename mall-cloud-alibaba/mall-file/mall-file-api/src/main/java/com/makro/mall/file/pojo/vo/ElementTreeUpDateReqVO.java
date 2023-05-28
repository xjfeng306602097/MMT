package com.makro.mall.file.pojo.vo;

import com.makro.mall.file.pojo.entity.MmElementTree;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ElementTreeUpDateReqVO extends MmElementTree {

    @NotNull
    private Boolean isPublic;
    @NotBlank
    private String elementType;
}
