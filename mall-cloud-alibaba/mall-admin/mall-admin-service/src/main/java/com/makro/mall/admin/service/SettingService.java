package com.makro.mall.admin.service;

import com.makro.mall.admin.pojo.entity.Setting;

/**
 * @author xiaojunfeng
 * @description 邮件服务
 * @date 2021/11/4
 */
public interface SettingService {

    Setting findFirstById(String id);

    Setting save(Setting message);

    Setting update(Setting message);

}
