package com.makro.mall.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.message.component.mail.MakroMailSender;
import com.makro.mall.message.pojo.entity.MessageProperties;
import com.makro.mall.message.repository.MessagePropertiesRepository;
import com.makro.mall.message.service.MessagePropertiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePropertiesServiceImpl implements MessagePropertiesService {

    private final MessagePropertiesRepository messagePropertiesRepository;
    private final RedisUtils redisUtils;


    @Override
    public MessageProperties findFirstById(String id) {
        Map<Object, Object> hmget = redisUtils.hmget(RedisConstants.MESSAGE_PROPERTIES_PREFIX);
        if (MapUtil.isNotEmpty(hmget)) {
            return BeanUtil.toBeanIgnoreError(hmget, MessageProperties.class);
        } else {
            MessageProperties messageProperties = messagePropertiesRepository.findFirstById(id);
            redisUtils.hmset(RedisConstants.MESSAGE_PROPERTIES_PREFIX, JSON.parseObject(JSON.toJSONString(messageProperties)));
            return messageProperties;
        }
    }

    @Override
    public MessageProperties save(MessageProperties properties) {
        properties = messagePropertiesRepository.save(properties);
        return properties;
    }


    @Override
    public MessageProperties update(MessageProperties properties) {
        MessageProperties dbMailMessage = messagePropertiesRepository.findFirstById(properties.getId());
        Assert.isTrue(dbMailMessage != null, StatusCode.EMAIL_NOT_EXISTS);
        BeanUtil.copyProperties(properties, dbMailMessage, CopyOptions.create().ignoreNullValue());
        messagePropertiesRepository.save(dbMailMessage);

        redisUtils.hmset(RedisConstants.MESSAGE_PROPERTIES_PREFIX, JSON.parseObject(JSON.toJSONString(dbMailMessage)));
        return dbMailMessage;
    }

    @Override
    public MakroMailSender getSenderByEmailAddress() {
        MessageProperties messageProperties = findFirstById("1");
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername(messageProperties.getMailUserName());
        javaMailSender.setPassword(messageProperties.getMailPassWord());
        javaMailSender.setPort(Integer.parseInt(messageProperties.getMailPort()));
        javaMailSender.setHost(messageProperties.getMailHost());
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", messageProperties.getMailHost());
        props.setProperty("mail.transport.protocol", messageProperties.getMailProtocol());
        props.setProperty("mail.smtp.auth", messageProperties.getMailAuth());
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.required", "true");
        props.setProperty("mail.smtp.ssl.enable", "true");
        props.setProperty("mail.smtp.socketFactory.port", messageProperties.getMailPort());
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.connectiontimeout", messageProperties.getMailConnectionTimeout());
        props.setProperty("mail.smtp.timeout", messageProperties.getMailTimeout());
        props.setProperty("socksProxyHost", messageProperties.getMailProxyHost());
        props.setProperty("socksProxyPort", messageProperties.getMailProxyPort());
        javaMailSender.setJavaMailProperties(props);
        return MakroMailSender.init(javaMailSender, Integer.parseInt(messageProperties.getMailLimit()));
    }


}
