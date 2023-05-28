package com.makro.mall.template.repository;

import com.makro.mall.mongo.repository.MongoTemplateAware;
import com.makro.mall.template.pojo.entity.MmUserCache;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author xiaojunfeng
 * @description 组件版本dao层
 * @date 2021/10/28
 */
public interface MmUserCacheRepository extends MongoRepository<MmUserCache, String>, MongoTemplateAware {

    MmUserCache findFirstByUsername(String username);

}
