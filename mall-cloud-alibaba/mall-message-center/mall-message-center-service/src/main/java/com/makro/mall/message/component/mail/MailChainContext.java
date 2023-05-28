package com.makro.mall.message.component.mail;

import com.makro.mall.message.pojo.dto.MailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 邮件责任链容器
 * @date 2021/11/4
 */
@Component
public class MailChainContext {

    @Autowired
    private List<MailSenderChain> chains;

    public void process(MailMessageDTO mailMessageDTO) {
        for (MailSenderChain chain : chains) {
            if (chain.accept(mailMessageDTO)) {
                chain.send(mailMessageDTO);
            }
        }
    }

}
