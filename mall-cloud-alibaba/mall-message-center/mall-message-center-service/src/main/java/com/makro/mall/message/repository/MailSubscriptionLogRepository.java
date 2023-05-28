package com.makro.mall.message.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.message.pojo.entity.MailSubscriptionLog;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 组件dao层
 * @date 2021/10/27
 */
public interface MailSubscriptionLogRepository extends MongoRepository<MailSubscriptionLog, String>, MongoTemplateAware {

    default Page<MailSubscriptionLog> page(String address, Date begin, Date end, Integer page,
                                           Integer size) {
        page = page - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate")
                .and(Sort.by(Sort.Direction.DESC, "gmtModified"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if (StrUtil.isNotEmpty(address)) {
            criteriaList.add(Criteria.where("address").regex(address));
        }
        if (begin != null && end != null) {
            criteriaList.add(Criteria.where("gmtCreate").gte(begin));
            criteriaList.add(Criteria.where("gmtCreate").lt(end));
        }
        if (CollectionUtil.isNotEmpty(criteriaList)) {
            criteria.andOperator(criteriaList);
            query.addCriteria(criteria);
        }
        long total = mongoTemplate().count(query, MailSubscriptionLog.class);
        List<MailSubscriptionLog> items = mongoTemplate().find(query.with(pageable), MailSubscriptionLog.class);
        return new PageImpl<>(items, pageable, total);
    }

}
