package com.makro.mall.product.pojo.vo;

import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdBrand;
import com.makro.mall.product.pojo.entity.ProdBrandPic;
import lombok.Data;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/25
 **/
@Data
public class ProBrandVO extends ProdBrand {

    private PicProperties<ProdBrandPic> pic;

}
