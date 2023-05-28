package com.makro.mall.stat.pojo.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/27
 */
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageViewRecord extends AbstractLogRecord {

    private String memberNo;

    private String memberType;

    private String mmCode;

    private String storeCode;

    private String channel;

    private String publishType;

    private String pageNo;

    private String pageUrl;

    private Integer isNew;

    private String pageType;

}
