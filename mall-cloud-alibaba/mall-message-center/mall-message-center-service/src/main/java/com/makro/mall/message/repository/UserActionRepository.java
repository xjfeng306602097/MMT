package com.makro.mall.message.repository;

import com.makro.mall.message.pojo.entity.UserAction;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserActionRepository extends MongoRepository<UserAction, String>, MongoTemplateAware {

}
