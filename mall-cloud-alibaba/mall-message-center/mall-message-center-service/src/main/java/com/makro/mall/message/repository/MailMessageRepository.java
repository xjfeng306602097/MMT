package com.makro.mall.message.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 组件dao层
 * @date 2021/10/27
 */
public interface MailMessageRepository extends MongoRepository<MailMessage, String>, MongoTemplateAware {

    MailMessage findFirstById(String id);

    default Page<MailMessage> page(List<Integer> status, Integer page,
                                   Integer size, Integer isDelete, String mmPublishJobEmailTaskId, String[] toUser) {
        page = page - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate")
                .and(Sort.by(Sort.Direction.DESC, "gmtModified"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (CollectionUtil.isNotEmpty(status)) {
            criteria.andOperator(
                    Criteria.where("status").in(status)
            );
        }
        if (isDelete != null) {
            criteria.andOperator(
                    Criteria.where("isDelete").is(isDelete)
            );
        }
        if (mmPublishJobEmailTaskId != null) {
            criteria.andOperator(
                    Criteria.where("mmPublishJobEmailTaskId").is(mmPublishJobEmailTaskId)
            );
        }
        if (toUser != null) {
            criteria.and("toUser").in(toUser);
        }
        query.addCriteria(criteria);
        long total = mongoTemplate().count(query, MailMessage.class);
        List<MailMessage> items = mongoTemplate().find(query.with(pageable), MailMessage.class);
        return new PageImpl<MailMessage>(items, pageable, total);
    }


}
