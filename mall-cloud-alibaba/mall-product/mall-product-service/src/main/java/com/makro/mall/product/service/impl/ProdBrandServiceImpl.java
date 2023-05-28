package com.makro.mall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ProdBrandMapper;
import com.makro.mall.product.pojo.entity.ProdBrand;
import com.makro.mall.product.pojo.entity.ProdBrandPic;
import com.makro.mall.product.service.ProdBrandPicService;
import com.makro.mall.product.service.ProdBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class ProdBrandServiceImpl extends ServiceImpl<ProdBrandMapper, ProdBrand>
        implements ProdBrandService {
    private final ProdBrandPicService prodBrandPicService;

    @Override
    public List<String> getBrandNames() {
        return this.baseMapper.getBrandNames();
    }

    @Override
    public IPage<ProdBrand> list(Page<ProdBrand> page, ProdBrand brand) {
        String name = brand.getName();
        Integer isvalid = brand.getIsvalid();
        LambdaQueryWrapper<ProdBrand> qw = new LambdaQueryWrapper<ProdBrand>()
                .eq(isvalid != null, ProdBrand::getIsvalid, isvalid)
                .eq(StrUtil.isNotBlank(name), ProdBrand::getName, name);
        return this.baseMapper.selectPage(page, qw);
    }

    @Override
    public Boolean update(String id, ProdBrand brand) {
        brand.setId(id);
        Boolean result = updateById(brand);

        if (brand.getPicid() != null) {
            changeDefault(id, brand);
        }
        return result;
    }

    /**
     * 功能描述: PROD_BRAND设置默认处理逻辑 在更新ProdBRAND的picId时触发
     * 1.将BRANDId内的默认取消
     * 2.将当前图片设置为默认
     *
     * @Param: id = PROD_BRAND 的ID = PROD_BRAND_PIC的BRANDId
     * @Author: 卢嘉俊
     * @Date: 2022/6/20
     */
    private void changeDefault(String id, ProdBrand brand) {
        prodBrandPicService.update(new LambdaUpdateWrapper<ProdBrandPic>().eq(ProdBrandPic::getBrandid, id).eq(ProdBrandPic::getDefaulted, 1L).set(ProdBrandPic::getDefaulted, 0L));
        prodBrandPicService.update(new LambdaUpdateWrapper<ProdBrandPic>().eq(ProdBrandPic::getId, brand.getPicid()).set(ProdBrandPic::getDefaulted, 1L));
    }

}




