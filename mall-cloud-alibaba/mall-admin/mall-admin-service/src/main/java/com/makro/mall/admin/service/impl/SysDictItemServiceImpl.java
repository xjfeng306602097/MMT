package com.makro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.SysDictItemMapper;
import com.makro.mall.admin.pojo.entity.SysDictItem;
import com.makro.mall.admin.service.SysDictItemService;
import org.springframework.stereotype.Service;

/**
 * @author xiaojunfeng
 * @description 字典明细服务
 * @date 2021/10/13
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem>
        implements SysDictItemService {

    @Override
    public IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict) {
        return this.baseMapper.list(page, dict);
    }
}




