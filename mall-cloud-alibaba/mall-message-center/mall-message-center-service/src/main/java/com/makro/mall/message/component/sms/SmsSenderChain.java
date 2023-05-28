package com.makro.mall.message.component.sms;

import com.makro.mall.message.pojo.dto.SmsMessageDTO;

/**
 * @author xiaojunfeng
 * @description 责任链接口
 * @date 2022/7/21
 */
public interface SmsSenderChain {

    /**
     * 判断是否接受
     *
     * @param dto
     * @return
     */
    boolean accept(SmsMessageDTO dto);

    /**
     * 发送接口
     *
     * @param dto
     */
    void send(SmsMessageDTO dto);
}
