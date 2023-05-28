package com.makro.mall.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ProdAuxMapper;
import com.makro.mall.product.pojo.entity.ProdAux;
import com.makro.mall.product.service.ProdAuxService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ProdAuxServiceImpl extends ServiceImpl<ProdAuxMapper, ProdAux>
        implements ProdAuxService {

    @Override
    public List<String> getAuxType() {
        return this.baseMapper.getAuxType();
    }

    @Override
    public IPage<ProdAux> list(Page<ProdAux> page, ProdAux aux) {
        List<ProdAux> list = this.baseMapper.list(page, aux);
        page.setRecords(list);
        return page;
    }

    @Override
    public boolean saveIfNotExist(ProdAux aux) {
        return this.baseMapper.saveIfNotExist(aux);
    }
}




