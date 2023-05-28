package com.makro.mall.admin.repository;

import com.makro.mall.admin.pojo.entity.Setting;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: 卢嘉俊
 */
public interface SettingRepository extends MongoRepository<Setting, String>, MongoTemplateAware {


    Setting findFirstById(String s);
}
