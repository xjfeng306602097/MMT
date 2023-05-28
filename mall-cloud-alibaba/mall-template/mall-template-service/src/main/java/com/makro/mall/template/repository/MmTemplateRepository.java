package com.makro.mall.template.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.mongo.repository.MongoTemplateAware;
import com.makro.mall.template.pojo.entity.MmTemplate;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 组件dao层
 * @date 2021/10/27
 */
public interface MmTemplateRepository extends MongoRepository<MmTemplate, String>, MongoTemplateAware {

    MmTemplate findFirstById(String id);

    MmTemplate findFirstByCode(String code);

    MmTemplate findFirstByMmCodeAndIsDelete(String mmCode, Integer isDelete);

    default Page<MmTemplate> page(List<Integer> status, String mmCode, String name, Date begin, Date end, Integer page,
                                  Integer size, Integer isDelete, String creator, String lastUpdater,
                                  BigDecimal pageWidth, BigDecimal pageHeight, Long configDpi, Long configUnitID, Boolean release, String pageOption) {
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
        if (StrUtil.isNotBlank(mmCode)) {
            criteriaList.add(Criteria.where("mmCode").is(mmCode));
        } else {
            criteriaList.add(Criteria.where("mmCode").exists(false));
        }
        if (StrUtil.isNotBlank(name)) {
            criteriaList.add(Criteria.where("name").regex(name));
        }
        if (StrUtil.isNotBlank(creator)) {
            criteriaList.add(Criteria.where("creator").is(creator));
        }
        if (StrUtil.isNotBlank(lastUpdater)) {
            criteriaList.add(Criteria.where("lastUpdater").is(lastUpdater));
        }
        if (StrUtil.isNotBlank(pageOption)) {
            criteriaList.add(Criteria.where("pageOption").is(pageOption));
        }
        if (begin != null && end != null) {
            criteriaList.add(Criteria.where("gmtCreate").gte(begin));
            criteriaList.add(Criteria.where("gmtCreate").lt(end));
        }
        if (isDelete != null) {
            criteriaList.add(Criteria.where("isDelete").is(isDelete));
        }
        if (pageWidth != null) {
            criteriaList.add(Criteria.where("pageWidth").is(pageWidth));
        }
        if (pageHeight != null) {
            criteriaList.add(Criteria.where("pageHeight").is(pageHeight));
        }
        if (configDpi != null) {
            criteriaList.add(Criteria.where("configDpi").is(configDpi));
        }
        if (configUnitID != null) {
            criteriaList.add(Criteria.where("configUnitID").is(configUnitID));
        }
        if (release != null) {
            criteriaList.add(Criteria.where("release").is(release));
        }
        if (CollectionUtil.isNotEmpty(criteriaList)) {
            criteria.andOperator(criteriaList);
            query.addCriteria(criteria);
        }
        long total = mongoTemplate().count(query, MmTemplate.class);
        List<MmTemplate> items = mongoTemplate().find(query.with(pageable), MmTemplate.class);
        return new PageImpl<MmTemplate>(items, pageable, total);
    }


    List<MmTemplate> findByMmCodeInAndIsDelete(List<String> mmcodes, int isDeleted);

    List<MmTemplate> findByIsDelete(int i);
}
