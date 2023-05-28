package com.makro.mall.message.service;

import com.makro.mall.message.CustomSpringbootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaojunfeng
 * @description 邮件服务测试类
 * @date 2021/11/5
 */
@CustomSpringbootTest
public class MailMessageServiceTest {

    @Autowired
    private MailMessageService mailMessageService;

    @Test
    @DisplayName("测试创建邮件发送任务")
    public void add() throws InterruptedException {
       /* MailMessage mailMessage = new MailMessage();
        mailMessage.setSuccessCount(0);
        mailMessage.setDelay(60l);
        mailMessage.setSubject("TEST");
        mailMessage.setToUser(new String[]{"a306602097a@163.com"});
        mailMessage.setContent("你好吗");
        mailMessage.setMailTypeEnum(MailTypeEnum.TEXT);
       *//* mailMessage.setMailTypeEnum(MailTypeEnum.H5);
        MailMessage.H5MailInfo h5MailInfo = new MailMessage.H5MailInfo();
        h5MailInfo.setPath("http://10.58.5.152:9000/makro-htmls/38129b1cbe9946bc8c59d34008456985.html");
        mailMessage.setH5MailInfo(h5MailInfo);*//*
        mailMessageService.save(mailMessage);*/
    }

}
