package com.makro.mall.product.controller.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.service.ProdListService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author Ljj
 */
@Api(tags = "关联MM的商品列表", hidden = true)
@RestController
@RequestMapping("/api/v1/prod/list/client")
@RequiredArgsConstructor
public class ProdListClientController {


    private final ProdListService prodListService;


    @PostMapping(value = "getMapByMmCodes")
    public BaseResponse<Map<String, List<ProdList>>> getMapByMmCodes(@RequestBody Set<String> keySet) {
        if (CollUtil.isEmpty(keySet)){
            return BaseResponse.success(MapUtil.empty());
        }
        List<ProdList> list = prodListService.list(new LambdaQueryWrapper<ProdList>()
                .in(ProdList::getMmCode, keySet)
                .eq(ProdList::getIsvalid, 1));
        Map<String, List<ProdList>> map = new HashMap<>(256);
        for (ProdList prodList : list) {
            List<ProdList> list1 = map.containsKey(prodList.getMmCode()) ? map.get(prodList.getMmCode()) : new ArrayList<>();
            list1.add(prodList);
            map.put(prodList.getMmCode(), list1);
        }
        return BaseResponse.success(map);
    }
}
