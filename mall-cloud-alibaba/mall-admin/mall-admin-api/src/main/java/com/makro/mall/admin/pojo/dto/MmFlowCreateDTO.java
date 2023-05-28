package com.makro.mall.admin.pojo.dto;

import com.makro.mall.admin.pojo.entity.MmFlow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/4/13
 */
@Data
public class MmFlowCreateDTO extends MmFlow {

    @ApiModelProperty(value = "备注", required = true)
    private String htmlRemark;

    @ApiModelProperty(value = "附件文件路径,用逗号隔开")
    private String filePath;

    @ApiModelProperty(value = "图片文件路径，用逗号隔开")
    private String imageList;

}
