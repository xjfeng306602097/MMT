package com.makro.mall.message.pojo.entity;

import com.makro.mall.mongo.model.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author xiaojunfeng
 * @description 视图访问监控数据
 * @date 2022/3/23
 */
@Data
@Document("stat_view")
public class StatView extends BaseEntity {

    @Indexed
    private String userId;

    @Indexed
    private String userName;

    private String view;

    private Date viewTime;

    private String ip;

}
