package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.makro.mall.admin.pojo.entity.Setting;
import com.makro.mall.admin.repository.SettingRepository;
import com.makro.mall.admin.service.SettingService;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.common.web.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;
    private final RedisUtils redisUtils;


    @Override
    public Setting findFirstById(String id) {
        Map<Object, Object> hmget = redisUtils.hmget(RedisConstants.SETTING_PREFIX);
        if (MapUtil.isNotEmpty(hmget)) {
            return BeanUtil.toBeanIgnoreError(hmget, Setting.class);
        } else {
            Setting setting = settingRepository.findFirstById(id);
            redisUtils.hmset(RedisConstants.SETTING_PREFIX, JSON.parseObject(JSON.toJSONString(setting)));
            return setting;
        }
    }

    @Override
    public Setting save(Setting properties) {
        return settingRepository.save(properties);
    }


    @Override
    public Setting update(Setting setting) {
        setting.setId("1");
        setting.setLastUpdater(JwtUtils.getUsername());
        Setting dbSetting = settingRepository.findFirstById("1");
        Assert.isTrue(dbSetting != null, AdminStatusCode.NOT_FOUND);
        BeanUtil.copyProperties(setting, dbSetting, CopyOptions.create().ignoreNullValue());
        settingRepository.save(dbSetting);

        redisUtils.hmset(RedisConstants.SETTING_PREFIX, JSON.parseObject(JSON.toJSONString(dbSetting)));
        return dbSetting;
    }


}
