package com.makro.mall.message.component.mail;

import com.makro.mall.message.pojo.dto.MailMessageDTO;

/**
 * @author xiaojunfeng
 * @description 责任链接口
 * @date 2021/11/4
 */
public interface MailSenderChain {

    boolean accept(MailMessageDTO dto);

    void send(MailMessageDTO dto);
}
