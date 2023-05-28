package com.makro.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ProdTemplateDetailsMapper;
import com.makro.mall.product.pojo.entity.ProdTemplateDetails;
import com.makro.mall.product.service.ProdTemplateDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProdTemplateDetailsServiceImpl extends ServiceImpl<ProdTemplateDetailsMapper, ProdTemplateDetails>
        implements ProdTemplateDetailsService {

    @Override
    public Boolean saveProdTemplateDetails(ProdTemplateDetails templateDetails) {
        Boolean result = this.save(templateDetails);
        return result;
    }

    @Override
    public Boolean saveBatchProdTemplateDetails(List<ProdTemplateDetails> list) {
        Boolean result = this.saveBatch(list);
        return result;
    }
}




