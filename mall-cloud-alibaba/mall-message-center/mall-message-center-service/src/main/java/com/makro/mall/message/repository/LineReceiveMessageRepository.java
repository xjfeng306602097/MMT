package com.makro.mall.message.repository;

import com.makro.mall.message.pojo.entity.LineReceiveMessage;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/7/26 Line
 */
public interface LineReceiveMessageRepository extends MongoRepository<LineReceiveMessage, String>, MongoTemplateAware {


}
