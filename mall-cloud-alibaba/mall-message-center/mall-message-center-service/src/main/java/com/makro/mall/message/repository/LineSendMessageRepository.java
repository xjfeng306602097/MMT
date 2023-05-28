package com.makro.mall.message.repository;

import cn.hutool.core.util.ObjectUtil;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/7/26 Line
 */
public interface LineSendMessageRepository extends MongoRepository<LineSendMessage, String>, MongoTemplateAware {


    default Page<LineSendMessage> page(MessagePageReqVO vo) {

        String success = vo.getStatus().getStatus();
        Integer page = vo.getPage();
        Integer size = vo.getSize();
        String mmPublishJobLineTaskId = vo.getMmPublishJobTaskId();
        String[] to = vo.getTo();


        page = page - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "now");
        Pageable pageable = PageRequest.of(page, size, sort);
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (ObjectUtil.isNotNull(success)) {
            criteria.andOperator(
                    Criteria.where("success").in(success)
            );
        }

        if (mmPublishJobLineTaskId != null) {
            criteria.andOperator(
                    Criteria.where("mmPublishJobLineTaskId").is(mmPublishJobLineTaskId)
            );
        }
        if (to != null) {
            criteria.and("to").in(to);
        }
        query.addCriteria(criteria);
        long total = mongoTemplate().count(query, LineSendMessage.class);
        List<LineSendMessage> items = mongoTemplate().find(query.with(pageable), LineSendMessage.class);
        return new PageImpl<LineSendMessage>(items, pageable, total);
    }

    List<LineSendMessage> findAllByMmPublishJobLineTaskId(String id);

}
