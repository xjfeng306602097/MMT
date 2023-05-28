package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmFlow;

/**
 *
 */
public interface MmFlowService extends IService<MmFlow> {

    MmFlow getLastOne(String code);
}
