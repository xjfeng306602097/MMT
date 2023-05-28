package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmFlowConfigMapper;
import com.makro.mall.admin.pojo.entity.MmFlowConfig;
import com.makro.mall.admin.pojo.entity.MmFlowConfigItem;
import com.makro.mall.admin.service.MmFlowConfigItemService;
import com.makro.mall.admin.service.MmFlowConfigService;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class MmFlowConfigServiceImpl extends ServiceImpl<MmFlowConfigMapper, MmFlowConfig>
        implements MmFlowConfigService {

    private final MmFlowConfigItemService mmFlowConfigItemService;

    @Override
    public Map<MmFlowConfig, List<MmFlowConfigItem>> getConfigItems() {
        List<MmFlowConfig> configs = list();
        Assert.isTrue(CollectionUtil.isNotEmpty(configs), StatusCode.MM_FLOW_FORBIDDEN);
        Map<Long, MmFlowConfig> map = configs.stream().collect(Collectors.toMap(MmFlowConfig::getId, v -> v));
        List<MmFlowConfigItem> items = mmFlowConfigItemService.list(new LambdaQueryWrapper<MmFlowConfigItem>()
                .in(MmFlowConfigItem::getRelateConfig, configs.stream().map(MmFlowConfig::getId).collect(Collectors.toList()))
                .orderByAsc(MmFlowConfigItem::getStep));
        Map<MmFlowConfig, List<MmFlowConfigItem>> itemMap = new HashMap<>();
        items.forEach(item -> {
            List<MmFlowConfigItem> list = itemMap.computeIfAbsent(map.get(item.getRelateConfig()), (k) -> {
                return new ArrayList<>();
            });
            list.add(item);
        });
        return itemMap;
    }

}




