package com.makro.mall.product.pojo.vo;

import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdIcon;
import com.makro.mall.product.pojo.entity.ProdIconPic;
import lombok.Data;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/25
 **/
@Data
public class ProdIconVO extends ProdIcon {
    private String IconIndex;
    private PicProperties<ProdIconPic> pic;

}
