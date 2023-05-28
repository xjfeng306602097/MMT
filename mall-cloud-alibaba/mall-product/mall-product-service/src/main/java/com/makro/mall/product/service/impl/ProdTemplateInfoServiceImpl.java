package com.makro.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ProdTemplateInfoMapper;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.service.ProdTemplateInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProdTemplateInfoServiceImpl extends ServiceImpl<ProdTemplateInfoMapper, ProdTemplateInfo>
        implements ProdTemplateInfoService {

    @Override
    public Boolean saveProdTemplateInfo(ProdTemplateInfo templateInfo) {
        Boolean result = this.save(templateInfo);
        return result;
    }
}




