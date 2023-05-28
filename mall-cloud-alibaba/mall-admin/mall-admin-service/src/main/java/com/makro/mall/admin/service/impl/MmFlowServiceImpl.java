package com.makro.mall.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmFlowMapper;
import com.makro.mall.admin.pojo.entity.MmFlow;
import com.makro.mall.admin.service.MmFlowService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MmFlowServiceImpl extends ServiceImpl<MmFlowMapper, MmFlow>
        implements MmFlowService {

    @Override
    public MmFlow getLastOne(String code) {
        return this.baseMapper.getLastOne(code);
    }
}




