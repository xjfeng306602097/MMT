package com.makro.mall.product.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.product.pojo.dto.MakroProProductDTO;
import com.makro.mall.product.pojo.dto.request.MakroProQueryReq;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.entity.ProdPrice;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jincheng
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ImportManager {
    private final ProdTemplateInfoService templateInfoService;
    private final ProdPriceService priceService;
    private final MmActivityFeignClient mmActivityFeignClient;
    private final ProdListService dataService;
    private final ProdStorageService storageService;
    private final MakroProService makroProService;

    @Transactional(rollbackFor = RuntimeException.class)
    public void importData(String mmCode, ProdTemplateInfo info, List<ProdList> prodListList) {
        List<ProdStorage> storageList = new ArrayList<>();
        List<ProdPrice> priceList = new ArrayList<>();
        MakroProQueryReq req = new MakroProQueryReq();
        req.setPage(1);
        req.setSize(1);
        req.setQuerySuggestions(true);
        prodListList.forEach(x -> {
            String dataId = IdUtil.simpleUUID();
            String itemCode = x.getUrlparam();

            // 保存即将入商品库的信息
            insertStorageList(storageList, info.getCreator(), x, dataId, itemCode);
            // 保存历史价格信息
            insertPriceList(priceList, x, dataId, itemCode);
            // 是否有传入productId,如果没有，默认调用查询接口返回
            if (x.getProductId() == null) {
                req.setQ(itemCode);
                IPage<MakroProProductDTO> dtos = makroProService.queryMakroProProduct(req);
                if (dtos.getSize() > 0) {
                    x.setProductId(dtos.getRecords().get(0).getProductId());
                }
            }
        });

        info.setMmCode(mmCode);
        templateInfoService.update(new LambdaUpdateWrapper<ProdTemplateInfo>().eq(ProdTemplateInfo::getIsvalid, 1).eq(ProdTemplateInfo::getMmCode, mmCode).set(ProdTemplateInfo::getIsvalid, 0));
        templateInfoService.saveProdTemplateInfo(info);

        priceService.update(new LambdaUpdateWrapper<ProdPrice>().eq(ProdPrice::getMmCode, mmCode).eq(ProdPrice::getIsvalid, 1).set(ProdPrice::getIsvalid, 0));
        priceService.saveBatch(priceList.stream().peek(x -> x.setMmCode(mmCode)).collect(Collectors.toList()));

        dataService.update(new LambdaUpdateWrapper<ProdList>().eq(ProdList::getMmCode, mmCode).eq(ProdList::getIsvalid, 1).set(ProdList::getIsvalid, 0));
        dataService.saveBatchProdList(prodListList.stream().peek(x -> x.setMmCode(mmCode)).collect(Collectors.toList()));


        storageService.batchSaveOrUpdateProdStorage(storageList);

        MmActivity activity = new MmActivity();
        activity.setPages(String.join(",", dataService.getPages(mmCode)));
        mmActivityFeignClient.updateByCode(mmCode, activity);
    }

    private void insertPriceList(List<ProdPrice> priceList, ProdList prodList, String dataId, String itemCode) {
        ProdPrice price = new ProdPrice();
        String priceId = IdUtil.simpleUUID();
        price.setId(priceId);
        price.setItemcode(itemCode);
        BigDecimal normalPrice = prodList.getNormalprice();
        BigDecimal promoPrice = prodList.getPromoprice();
        price.setNormalprice(normalPrice == null ? new BigDecimal("0") : normalPrice);
        price.setPromoprice(promoPrice == null ? new BigDecimal("0") : promoPrice);
        price.setIsvalid(1);
        price.setCreator(JwtUtils.getUsername());
        price.setDataid(dataId);
        priceList.add(price);
    }

    private void insertStorageList(List<ProdStorage> storageList, String username, ProdList prodList, String dataId, String itemCode) {
        ProdStorage storage = new ProdStorage();
        BeanUtil.copyProperties(prodList, storage);
        String storageId = Optional.ofNullable(itemCode).orElse(IdUtil.simpleUUID());
        // ItemCode作为唯一ID主键，届时入库依据该ID字段saveOrUpdate
        storage.setId(storageId);
        storage.setItemcode(storageId);
        storage.setCreator(username);
        storage.setIsvalid(1);
        storage.setLastmodifydataid(dataId);
        storageList.add(storage);
    }
}
