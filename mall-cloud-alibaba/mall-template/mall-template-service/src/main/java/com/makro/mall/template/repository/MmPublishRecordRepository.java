package com.makro.mall.template.repository;

import com.makro.mall.mongo.repository.MongoTemplateAware;
import com.makro.mall.template.pojo.entity.MmPublishRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author xiaojunfeng
 * @description 组件dao层
 * @date 2021/10/27
 */
public interface MmPublishRecordRepository extends MongoRepository<MmPublishRecord, String>, MongoTemplateAware {

    MmPublishRecord findFirstByCode(String code);

}
