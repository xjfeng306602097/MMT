package com.makro.mall.product.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.dto.MakroProProductDTO;
import com.makro.mall.product.pojo.dto.request.MakroProQueryReq;
import com.makro.mall.product.service.MakroProService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/4/24
 */
@Service
public class MakroProServiceImpl implements MakroProService {
    @Value("${makro.pro.search.url:https://search.maknet.siammakro.cloud/search/api/v1/indexes/products/search}")
    private String makroProSearchUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public IPage<MakroProProductDTO> queryMakroProProduct(MakroProQueryReq req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authority", "search.maknet.siammakro.cloud");
        HttpEntity<MakroProQueryReq> requestEntity = new HttpEntity<>(req, headers);
        JSONObject result = restTemplate.postForObject(makroProSearchUrl, requestEntity, JSONObject.class);
        Integer total = 0;
        if (result == null || ObjectUtil.equals(total = result.getInteger("found"), 0)) {
            return returnEmptyPage();
        }
        return returnMakroProItems(req, result, total);
    }

    @NotNull
    private IPage<MakroProProductDTO> returnMakroProItems(MakroProQueryReq req, JSONObject result, Integer total) {
        JSONArray hits = result.getJSONArray("hits");
        List<MakroProProductDTO> dtos = new ArrayList<>();
        for (int i = 0; i < hits.size(); i++) {
            JSONObject item = hits.getJSONObject(i).getJSONObject("document");
            MakroProProductDTO dto = item.toJavaObject(MakroProProductDTO.class);
            dtos.add(dto);
        }
        IPage<MakroProProductDTO> pages = new Page<MakroProProductDTO>();
        pages.setRecords(dtos);
        pages.setSize(req.getSize());
        pages.setTotal(total);
        pages.setCurrent(req.getPage());
        return pages;
    }

    @NotNull
    private IPage<MakroProProductDTO> returnEmptyPage() {
        IPage<MakroProProductDTO> pages = new Page<MakroProProductDTO>();
        pages.setPages(0);
        pages.setRecords(new ArrayList<>());
        pages.setSize(0);
        pages.setTotal(0);
        return pages;
    }
}
