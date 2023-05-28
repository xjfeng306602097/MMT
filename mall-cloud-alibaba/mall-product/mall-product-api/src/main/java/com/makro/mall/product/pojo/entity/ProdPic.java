package com.makro.mall.product.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 商品图片
 *
 * @TableName PROD_PIC
 */
@TableName(value = "PROD_PIC")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class ProdPic extends BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("商品code")
    private String itemCode;

    @ApiModelProperty("图片路径json")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String filePath;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;

    private Long defaulted;

    @TableLogic(delval = "1", value = "0")
    private Long deleted;

    @ApiModelProperty("商品是否临时")
    private Integer temp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
