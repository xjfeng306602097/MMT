package com.makro.mall.template.pojo.dto;

import com.makro.mall.template.pojo.entity.MmTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MmTemplatePageDTO extends MmTemplate {

    @ApiModelProperty(value = "设计器是否有人使用")
    private Boolean lock;

    @ApiModelProperty(value = "设计器使用者信息 该类是SysUser")
    private Object lockUser;
}
