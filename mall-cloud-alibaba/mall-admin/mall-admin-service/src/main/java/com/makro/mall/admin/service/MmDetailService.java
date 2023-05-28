package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmDetail;
import com.makro.mall.admin.pojo.vo.MmDetailVO;
import com.makro.mall.product.pojo.vo.ProductVO;

import java.util.List;

/**
 *
 */
public interface MmDetailService extends IService<MmDetail> {

    List<MmDetailVO<ProductVO>> listRelatedInfosByCode(String mmCode, Long pageSort, Long sort);

    boolean batchSave(String mmCode, List<MmDetail> details);
}
