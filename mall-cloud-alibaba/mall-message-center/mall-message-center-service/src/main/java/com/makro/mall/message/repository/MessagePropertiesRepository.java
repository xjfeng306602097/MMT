package com.makro.mall.message.repository;

import com.makro.mall.message.pojo.entity.MessageProperties;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/7/26 Line
 */
public interface MessagePropertiesRepository extends MongoRepository<MessageProperties, String>, MongoTemplateAware {


    MessageProperties findFirstById(String id);
}
