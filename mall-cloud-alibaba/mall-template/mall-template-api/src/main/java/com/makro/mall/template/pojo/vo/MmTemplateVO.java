package com.makro.mall.template.pojo.vo;

import com.makro.mall.template.pojo.dto.MmActivityDTO;
import com.makro.mall.template.pojo.entity.MmTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author xiaojunfeng
 * @description 模板vo对象
 * @date 2021/11/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MmTemplateVO extends MmTemplate {

    private BigDecimal unitInch;
    @ApiModelProperty(value = "MM信息")
    private MmActivityDTO mmInfo;


}
