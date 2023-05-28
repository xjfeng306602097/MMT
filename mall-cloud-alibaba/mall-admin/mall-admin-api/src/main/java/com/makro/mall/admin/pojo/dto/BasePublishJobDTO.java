package com.makro.mall.admin.pojo.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/3/10
 */
@Data
@ToString(callSuper = true)
public class BasePublishJobDTO {

    private PublishCondition sendList;

    private PublishCondition exceptList;


    @Data
    @ToString(callSuper = true)
    public static class PublishCondition {

        private Set<Long> segmentIds;

        private Set<String> memberTypeIds;

        private String customersS3Url;

    }


}
