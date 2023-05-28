package com.makro.mall.message.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaojunfeng
 * @description 组件dao层
 * @date 2021/10/27
 */
public interface SmsRepository extends MongoRepository<SmsMessage, String>, MongoTemplateAware {

    SmsMessage findFirstById(String id);

    default Page<SmsMessage> page(List<Integer> status, String sender, Date begin, Date end, Integer page,
                                  Integer size, Integer isDelete) {
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
        if (StrUtil.isNotBlank(sender)) {
            criteria.andOperator(
                    Criteria.where("sender").regex(sender)
            );
        }
        if (begin != null && end != null) {
            criteria.andOperator(
                    Criteria.where("gmtCreate").gte(begin),
                    Criteria.where("gmtCreate").lt(end)
            );
        }
        if (isDelete != null) {
            criteria.andOperator(
                    Criteria.where("isDelete").is(isDelete)
            );
        }
        query.addCriteria(criteria);
        long total = mongoTemplate().count(query, SmsMessage.class);
        List<SmsMessage> items = mongoTemplate().find(query.with(pageable), SmsMessage.class);
        return new PageImpl<SmsMessage>(items, pageable, total);
    }


    default Page<SmsMessage> smsUserPage(MessagePageReqVO vo) {
        String status = null;
        if (vo.getStatus() != null) {
            status = vo.getStatus().getStatus();

        }

        Integer page = vo.getPage();
        Integer size = vo.getSize();
        String mmPublishJobTaskId = vo.getMmPublishJobTaskId();
        String[] to = vo.getTo();

        page = page - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate")
                .and(Sort.by(Sort.Direction.DESC, "gmtModified"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(status)) {
            criteria.andOperator(
                    Criteria.where("status").in(status)
            );
        }
        if (to != null) {
            criteria.and("receivers").in(to);
        }
        if (mmPublishJobTaskId != null) {
            criteria.andOperator(
                    Criteria.where("mmPublishJobSmsTaskId").is(Long.valueOf(mmPublishJobTaskId))
            );
        }
        query.addCriteria(criteria);
        long total = mongoTemplate().count(query, SmsMessage.class);
        List<SmsMessage> items = mongoTemplate().find(query.with(pageable), SmsMessage.class);
        return new PageImpl<SmsMessage>(items, pageable, total);
    }
}
