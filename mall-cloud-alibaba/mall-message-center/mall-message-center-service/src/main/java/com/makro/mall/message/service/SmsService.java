package com.makro.mall.message.service;

import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 邮件服务
 * @date 2021/11/4
 */
public interface SmsService {

    Page<SmsMessage> page(List<Integer> status, String sender, Date begin, Date end, Integer page,
                          Integer size, Integer isDelete);

    SmsMessage findFirstById(String id);

    SmsMessage save(SmsMessage message);

    SmsMessage update(SmsMessage message);

    Long removeByIds(List<String> ids);

    Page<SmsMessage> smsUserPage(MessagePageReqVO vo);

    void updateTaskStatus(Long mmPublishJobSmsTaskId, MessageSendEnum status, List<String> receivers);

    void updateTaskUserStatus(Long mmPublishJobSmsTaskId, MessageSendEnum status, List<String> receivers);
}
