package com.makro.mall.message.component;

import com.makro.mall.message.CustomSpringbootTest;
import com.makro.mall.message.component.mail.DefaultMailSender;
import com.makro.mall.message.pojo.dto.H5MailMessageDTO;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.service.MailMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaojunfeng
 * @description 邮件发送器
 * @date 2021/11/3
 */
@CustomSpringbootTest
public class DefaultMailSenderTest {

    @Autowired
    private DefaultMailSender defaultMailSender;

    @Autowired
    private MailMessageService mailMessageService;

    @Test
    @DisplayName("发送邮件")
    void send() throws InterruptedException {
        MailMessage message = mailMessageService.findFirstById("61855764bf4d8f117cf1fcef");
        if (message != null) {
            H5MailMessageDTO dto = new H5MailMessageDTO(message.getMailTypeEnum());
            dto.setId(message.getId());
            dto.setSender(message.getSender());
            dto.setToUser(message.getToUser());
            dto.setSubject(message.getSubject());
            dto.setContent(message.getContent());
            dto.setMailTypeEnum(message.getMailTypeEnum());
            dto.setPath(message.getH5MailInfo().getPath());
            defaultMailSender.send(dto);
        }
    }


}
