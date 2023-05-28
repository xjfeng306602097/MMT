package com.makro.mall.template.repository;

import com.makro.mall.mongo.repository.MongoTemplateAware;
import com.makro.mall.template.pojo.entity.MmTemplateDraft;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 组件版本dao层
 * @date 2021/10/28
 */
public interface MmTemplateDraftRepository extends MongoRepository<MmTemplateDraft, String>, MongoTemplateAware {

    MmTemplateDraft findMmTemplateDraftByCodeAndAndVersion(String code, Integer version);

    default List<MmTemplateDraft> getDraftInfos(String code) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        query.fields().exclude("content", "templatePageList");
        return mongoTemplate().find(query, MmTemplateDraft.class);
    }

}
