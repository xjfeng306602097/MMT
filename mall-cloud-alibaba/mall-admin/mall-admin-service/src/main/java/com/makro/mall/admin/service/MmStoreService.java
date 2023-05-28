package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmStore;

import java.util.List;

/**
 *
 */
public interface MmStoreService extends IService<MmStore> {

    List<MmStore> getList(String code, Integer status, String name, Integer isDeleted);

    void syncFromMakro();
}
