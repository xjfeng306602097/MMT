package com.makro.mall.mongo.repository;

import com.makro.mall.common.util.SpringContextHolder;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author xiaojunfeng
 * @since 2021/4/14
 */
public interface MongoTemplateAware {

    default MongoTemplate mongoTemplate() {
        return SpringContextHolder.getBean(MongoTemplate.class);
    }

}
