package com.makro.mall.message.service;

import com.makro.mall.message.component.mail.MakroMailSender;
import com.makro.mall.message.pojo.entity.MessageProperties;

/**
 * @author xiaojunfeng
 * @description 邮件服务
 * @date 2021/11/4
 */
public interface MessagePropertiesService {

    MessageProperties findFirstById(String id);

    MessageProperties save(MessageProperties message);

    MessageProperties update(MessageProperties message);

    MakroMailSender getSenderByEmailAddress();
}
