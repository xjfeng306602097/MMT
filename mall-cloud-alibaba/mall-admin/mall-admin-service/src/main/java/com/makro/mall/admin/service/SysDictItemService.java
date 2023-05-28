package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysDictItem;

/**
 *
 */
public interface SysDictItemService extends IService<SysDictItem> {

    IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict);

}
