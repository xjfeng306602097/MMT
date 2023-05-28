package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.product.pojo.dto.MakroProProductDTO;
import com.makro.mall.product.pojo.dto.request.MakroProQueryReq;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/4/24
 */
public interface MakroProService {

    IPage<MakroProProductDTO> queryMakroProProduct(MakroProQueryReq req);

}
