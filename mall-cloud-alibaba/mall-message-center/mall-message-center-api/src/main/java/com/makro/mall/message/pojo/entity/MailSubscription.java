package com.makro.mall.message.pojo.entity;

import com.makro.mall.mongo.model.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/1/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("mail_subscription")
public class MailSubscription extends BaseEntity {

    public static final Integer STATUS_NORMAL = 1;

    public static final Integer STATUS_OFF_LINE = 2;


    @Indexed
    @NotNull
    private String address;

    /**
     * 0-未订阅，1-已订阅，2-取消订阅
     */
    private Integer status;

}
