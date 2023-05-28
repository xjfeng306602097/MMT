package com.makro.mall.file.pojo.vo;

import com.makro.mall.file.pojo.entity.MmElementTree;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ElementTreeSaveReqVO {
    private List<MmElementTree> element;
    @NotNull
    private Boolean isPublic;
}
