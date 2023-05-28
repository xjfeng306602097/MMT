package com.makro.mall.admin.repository;

import com.makro.mall.admin.pojo.entity.MallWithProductLog;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: 卢嘉俊
 */
public interface MallWithProductLogRepository extends MongoRepository<MallWithProductLog, String>, MongoTemplateAware {


}
