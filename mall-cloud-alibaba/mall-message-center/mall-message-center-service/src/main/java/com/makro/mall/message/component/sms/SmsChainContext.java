package com.makro.mall.message.component.sms;

import com.makro.mall.message.pojo.dto.SmsMessageDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description sms责任链容器
 * @date 2022/7/21
 */
@Component
public class SmsChainContext {

    @Resource
    private List<SmsSenderChain> chains;

    public void process(SmsMessageDTO messageDTO) {
        for (SmsSenderChain chain : chains) {
            if (chain.accept(messageDTO)) {
                chain.send(messageDTO);
            }
        }
    }

}
