package com.makro.mall.message.component.mail;

import com.makro.mall.message.pojo.dto.MailMessageDTO;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description AWS邮件服务
 * @date 2021/11/4
 */
@Component
public class AwsSesSender implements MailSenderChain {

    private static final String NAME_SPACE = "aws";

    @Override
    public boolean accept(MailMessageDTO dto) {
        return NAME_SPACE.equals(dto.getSender());
    }

    @Override
    public void send(MailMessageDTO dto) {
        // TODO AWS邮件发送
    }
}
