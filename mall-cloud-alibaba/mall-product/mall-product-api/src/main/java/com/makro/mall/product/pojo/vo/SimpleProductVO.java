package com.makro.mall.product.pojo.vo;

import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdStorage;
import lombok.Data;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/12/12
 **/
@Data
public class SimpleProductVO {
    private int sort;
    private ProdStorage info;
    private PicProperties<ProdPicVO> pic;
}
