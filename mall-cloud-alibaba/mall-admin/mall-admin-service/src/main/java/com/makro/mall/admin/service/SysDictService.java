package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysDict;

import java.util.List;

public interface SysDictService extends IService<SysDict> {

    Boolean batchDelete(List<Integer> idList);
}
