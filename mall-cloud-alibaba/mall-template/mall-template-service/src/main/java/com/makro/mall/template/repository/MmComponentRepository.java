package com.makro.mall.template.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import com.makro.mall.template.pojo.entity.MmComponent;
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
public interface MmComponentRepository extends MongoRepository<MmComponent, String>, MongoTemplateAware {

    MmComponent findFirstById(String id);

    MmComponent findFirstByCode(String code);

    default Page<MmComponent> page(List<Integer> status, String name, Date begin, Date end, Integer page,
                                   Integer size, Integer isDelete) {
        page = page - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "gmtCreate")
                .and(Sort.by(Sort.Direction.DESC, "gmtModified"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(status)) {
            criteriaList.add(Criteria.where("status").in(status));
        }
        if (StrUtil.isNotBlank(name)) {
            criteriaList.add(Criteria.where("name").regex(name));
        }
        if (begin != null && end != null) {
            criteriaList.add(Criteria.where("gmtCreate").gte(begin));
            criteriaList.add(Criteria.where("gmtCreate").lt(end));
        }
        if (isDelete != null) {
            criteriaList.add(Criteria.where("isDelete").is(isDelete));
        }
        if (CollectionUtil.isNotEmpty(criteriaList)) {
            criteria.andOperator(criteriaList);
            query.addCriteria(criteria);
        }
        long total = mongoTemplate().count(query, MmComponent.class);
        List<MmComponent> items = mongoTemplate().find(query.with(pageable), MmComponent.class);
        return new PageImpl<MmComponent>(items, pageable, total);
    }

}
