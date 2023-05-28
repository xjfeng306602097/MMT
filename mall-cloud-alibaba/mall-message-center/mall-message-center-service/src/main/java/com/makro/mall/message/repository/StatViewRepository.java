package com.makro.mall.message.repository;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.StatView;
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
 * @date 2022/3/23
 */
public interface StatViewRepository extends MongoRepository<StatView, String>, MongoTemplateAware {

    MailMessage findFirstById(String id);

    default Page<MailMessage> page(String userId, String userName, Date begin, Date end, Integer page,
                                   Integer size) {
        page = page - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate")
                .and(Sort.by(Sort.Direction.DESC, "gmtModified"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(userId)) {
            criteria.andOperator(
                    Criteria.where("userId").regex(userId)
            );
        }
        if (StrUtil.isNotBlank(userName)) {
            criteria.andOperator(
                    Criteria.where("userName").regex(userName)
            );
        }
        if (begin != null && end != null) {
            criteria.andOperator(
                    Criteria.where("gmtCreate").gte(begin),
                    Criteria.where("gmtCreate").lt(end)
            );
        }
        query.addCriteria(criteria);
        long total = mongoTemplate().count(query, MailMessage.class);
        List<MailMessage> items = mongoTemplate().find(query.with(pageable), MailMessage.class);
        return new PageImpl<MailMessage>(items, pageable, total);
    }


}
