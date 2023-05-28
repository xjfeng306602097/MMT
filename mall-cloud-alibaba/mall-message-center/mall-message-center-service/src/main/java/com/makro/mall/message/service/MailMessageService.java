package com.makro.mall.message.service;

import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.message.pojo.dto.SubscribeDTO;
import com.makro.mall.message.pojo.dto.UnsubscribeLogPageReq;
import com.makro.mall.message.pojo.dto.UnsubscribePageReq;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.MailSubscription;
import com.makro.mall.message.pojo.entity.MailSubscriptionLog;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 邮件服务
 * @date 2021/11/4
 */
public interface MailMessageService {

    Page<MailMessage> page(String status, Integer page,
                           Integer size, Integer isDelete, String mmPublishJobEmailTaskId, String[] toUser);

    MailMessage findFirstById(String id);

    MailMessage save(MailMessage message);

    void updateTaskStatus(String id, MessageSendEnum status, String[] toUser);

    MailMessage update(MailMessage message);

    Long removeByIds(List<String> ids);

    boolean unSubscribe(SubscribeDTO dto);

    Page<MailSubscriptionLog> unSubscribeLog(UnsubscribeLogPageReq req);

    Page<MailSubscription> unSubscribeList(UnsubscribePageReq req);

    void subScribe(SubscribeDTO dto);

    List<MailSubscription> listMailSubscriptionsByAddressAndStatus(List<String> addresses, Integer status);

    void updateTaskLogStatus(String mmPublishJobEmailTaskId, MessageSendEnum status, String[] toUser);
}
