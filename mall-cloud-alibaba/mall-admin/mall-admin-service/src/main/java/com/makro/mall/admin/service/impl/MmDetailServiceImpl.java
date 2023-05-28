package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmDetailMapper;
import com.makro.mall.admin.pojo.entity.MmDetail;
import com.makro.mall.admin.pojo.vo.MmDetailVO;
import com.makro.mall.admin.service.MmDetailService;
import com.makro.mall.product.api.ProdDataFeignClient;
import com.makro.mall.product.pojo.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class MmDetailServiceImpl extends ServiceImpl<MmDetailMapper, MmDetail>
        implements MmDetailService {

    private final ProdDataFeignClient prodDataFeignClient;

    @Override
    public List<MmDetailVO<ProductVO>> listRelatedInfosByCode(String mmCode, Long pageSort, Long sort) {
        // 1.查出mmCode相关的字段
        List<MmDetail> details = this.list(new LambdaQueryWrapper<MmDetail>().eq(MmDetail::getItemCode, mmCode)
                .eq(MmDetail::getIsValid, 1L)
                .eq(pageSort != null, MmDetail::getPageSort, pageSort)
                .eq(sort != null, MmDetail::getSort, sort));
        if (CollectionUtil.isNotEmpty(details)) {
            List<ProductVO> prodDataList = prodDataFeignClient.listByIds(details.stream()
                    .map(MmDetail::getProdDataId).distinct().collect(Collectors.joining(","))).getData();
            Map<String, ProductVO> prodDataMap = prodDataList.stream().collect(Collectors.toMap(k -> k.getInfo().getId(), v -> v));
            return details.stream().map(d -> {
                MmDetailVO<ProductVO> vo = new MmDetailVO<ProductVO>();
                BeanUtil.copyProperties(d, vo, CopyOptions.create().ignoreNullValue());
                vo.setProdData(prodDataMap.get(vo.getProdDataId()));
                return vo;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean batchSave(String mmCode, List<MmDetail> details) {
        details.forEach(d -> {
            d.setMmCode(mmCode);
        });
        return saveOrUpdateBatch(details);
    }

}




