package com.makro.mall.file.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElementTreeListRspVO {

    private List<ElementTreeVO> list;

    @ApiModelProperty("parentId再上一个Id")
    private Long previousId;
}
