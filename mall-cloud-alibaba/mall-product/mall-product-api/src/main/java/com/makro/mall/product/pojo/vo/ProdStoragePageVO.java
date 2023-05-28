package com.makro.mall.product.pojo.vo;

import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdIconPic;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdStoragePageVO {


    private String id;

    @ApiModelProperty("商品编码")
    private String itemcode;

    @ApiModelProperty("商品名")
    private String namethai;

    @ApiModelProperty("英文名称")
    private String nameen;

    @ApiModelProperty("销售单位")
    private String qty1unit;

    @ApiModelProperty("规格")
    private String pack;

    @ApiModelProperty("型号")
    private String model;

    private BigDecimal normalprice;

    private BigDecimal promoprice;

    @ApiModelProperty("类别编码")
    private Integer categoryid;

    @ApiModelProperty("最后更新关联的dataUUID")
    private String lastmodifydataid;

    @ApiModelProperty("isvalid")
    private Integer isvalid;

    @ApiModelProperty("图片地址")
    private PicProperties<ProdIconPic> pic;

}
