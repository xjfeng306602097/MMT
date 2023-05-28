package com.makro.mall.product.pojo.vo;

import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.dto.ProdInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/25
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    @ApiModelProperty(value = "商品基本信息")
    private ProdInfo info;
    @ApiModelProperty(value = "商品图片实体")
    private PicProperties pic;
    @ApiModelProperty(value = "图标实体列表")
    private List<ProdIconVO> icons;
    @ApiModelProperty(value = "关联商品")
    private List<SimpleProductVO> linkItems;
}
