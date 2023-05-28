package com.makro.mall.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ProdDataMapper;
import com.makro.mall.product.pojo.entity.ProdData;
import com.makro.mall.product.service.ProdDataService;
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
public class ProdDataServiceImpl extends ServiceImpl<ProdDataMapper, ProdData>
        implements ProdDataService {

    @Override
    public IPage<ProdData> list(Page<ProdData> page, ProdData data) {
        List<ProdData> list = this.baseMapper.list(page, data);
        page.setRecords(list);
        return page;
    }

}




