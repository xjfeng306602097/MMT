package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmFlowConfig;
import com.makro.mall.admin.pojo.entity.MmFlowConfigItem;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface MmFlowConfigService extends IService<MmFlowConfig> {

    Map<MmFlowConfig, List<MmFlowConfigItem>> getConfigItems();
}
