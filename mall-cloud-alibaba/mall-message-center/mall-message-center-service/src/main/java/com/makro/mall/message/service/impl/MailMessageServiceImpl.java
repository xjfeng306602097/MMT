package com.makro.mall.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson2.JSON;
import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskMonitorDTO;
import com.makro.mall.admin.pojo.dto.UserTaskMonitorDTO;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.MessageStatusCode;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.message.enums.MailSubEnum;
import com.makro.mall.message.mq.producer.MailMessageProducer;
import com.makro.mall.message.pojo.dto.*;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.MailSubscription;
import com.makro.mall.message.pojo.entity.MailSubscriptionLog;
import com.makro.mall.message.repository.MailMessageRepository;
import com.makro.mall.message.repository.MailSubscriptionLogRepository;
import com.makro.mall.message.repository.MailSubscriptionRepository;
import com.makro.mall.message.service.MailMessageService;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 邮件服务实现类
 * @date 2021/11/4
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailMessageServiceImpl implements MailMessageService {

    private final MailMessageRepository mailMessageRepository;
    private final MailMessageProducer mailMessageProducer;
    private final RedisUtils redisUtils;
    private final PublishFeignClient publishFeignClient;
    private final MailSubscriptionRepository mailSubscriptionRepository;

    private final MailSubscriptionLogRepository mailSubscriptionLogRepository;

    @Override
    public Page<MailMessage> page(String status, Integer page, Integer size, Integer isDelete, String mmPublishJobEmailTaskId, String[] toUser) {
        List<Integer> statusList = new ArrayList<>();
        if (StrUtil.isNotBlank(status)) {
            int[] arrays = Arrays.stream(status.split(", ")).mapToInt(Integer::parseInt).toArray();
            statusList = Arrays.stream(arrays).boxed().collect(Collectors.toList());
        }
        return mailMessageRepository.page(statusList, page, size, isDelete, mmPublishJobEmailTaskId, toUser);
    }

    @Override
    public MailMessage findFirstById(String id) {
        return mailMessageRepository.findFirstById(id);
    }

    @Override
    public MailMessage save(MailMessage message) {
        Assert.isTrue(message.getToUser() != null && message.getToUser().length > 0, StatusCode.TOUSER_NOT_NULL);
        Assert.isTrue(StrUtil.isNotBlank(message.getSubject()), StatusCode.SUBJECT_NOT_NULL);
        // 设置为未删除
        message.setIsDelete(0);
        // 初始化默认status=1
        message.setStatus(MessageSendEnum.NOT_SENT);
        message = mailMessageRepository.save(message);
        MailMessage finalMessage = message;
        if (message.getDelay() < MailMessage.MAIL_SEND_DELAY_THRESHOLD) {
            CompletableFuture.runAsync(() -> {
                switch (finalMessage.getMailTypeEnum()) {
                    case TEXT:
                        TextMailMessageDTO text = new TextMailMessageDTO();
                        BeanUtil.copyProperties(finalMessage, text);
                        mailMessageProducer.sendTextMailMessage(text, finalMessage.getDelay());
                        break;
                    case H5:
                    case H5_TEMPLATE:
                        H5MailMessageDTO h5 = new H5MailMessageDTO(finalMessage.getMailTypeEnum());
                        BeanUtil.copyProperties(finalMessage, h5);
                        BeanUtil.copyProperties(finalMessage.getH5MailInfo(), h5);
                        mailMessageProducer.sendH5MailMessage(h5, finalMessage.getDelay());
                        break;
                    default:
                        break;
                }
            }).exceptionally(e -> {
                log.error("MailMessageServiceImpl save执行失败 MailMessage:{} 原因:{}", JSON.toJSONString(finalMessage), e.toString());
                // 发送失败,异常处理,修改消息状态
                MailMessage mailMessage = new MailMessage();
                mailMessage.setId(finalMessage.getId());
                mailMessage.setStatus(MessageSendEnum.FAILED);
                update(mailMessage);

                updateTaskStatus(finalMessage.getMmPublishJobEmailTaskId(), MessageSendEnum.FAILED, finalMessage.getToUser());
                return null;
            });
        }
        return message;
    }


    /**
     * 功能描述:
     *
     * @Return:
     * @Author: 卢嘉俊
     * @Date: 2022/9/20 发布任务
     */
    @Override
    public void updateTaskStatus(String id, MessageSendEnum status, String[] toUser) {
        if (StrUtil.isNotEmpty(id) && ObjectUtil.isNotNull(redisUtils.get("email:job:" + id + ":total"))) {
            if (ObjectUtil.notEqual(status, MessageSendEnum.SUCCEEDED)) {
                log.info("email 发送给用户:{} 失败 taskId:{}", toUser, id);
                //失败
                redisUtils.incr("email:job:" + id + ":fail", 1);
            }
            long total = redisUtils.decr("email:job:" + id + ":total", 1);
            log.info("email 发送剩余:{} 用户:{} taskId:{}", total, toUser, id);
            if (ObjectUtil.equal(total, 0L)) {
                Integer fail = (Integer) redisUtils.get("email:job:" + id + ":fail");
                MmPublishJobTaskMonitorDTO dto1 = new MmPublishJobTaskMonitorDTO();
                dto1.setId(id);
                dto1.setChannel("email");

                //status -> 0-发布失败,1-未发布,2-发布中,3-发布成功,4-发布部分成功
                if (ObjectUtil.equal(fail, 0)) {
                    //如果失败为0则为全部成功
                    dto1.setStatus(MessageTaskEnum.SUCCEEDED.getStatus());
                } else if (ObjectUtil.equal(total - fail, 0L)) {
                    //总数-失败数不为0则为部分成功
                    dto1.setStatus(MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus());
                } else {
                    //失败
                    dto1.setStatus(MessageTaskEnum.FAILED.getStatus());
                }
                log.info("email 更新任务状态为:{} 用户:{} taskId:{}", dto1.getStatus(), toUser, id);
                publishFeignClient.updateState(dto1);
            }
        }
    }

    @Override
    public MailMessage update(MailMessage message) {
        MailMessage dbMailMessage = mailMessageRepository.findFirstById(message.getId());
        Assert.isTrue(dbMailMessage != null, StatusCode.EMAIL_NOT_EXISTS);
        BeanUtil.copyProperties(message, dbMailMessage, CopyOptions.create().ignoreNullValue());
        mailMessageRepository.save(dbMailMessage);
        return dbMailMessage;
    }

    @Override
    public Long removeByIds(List<String> ids) {
        Query query = Query.query(Criteria.where("id").in(ids));
        Update update = Update.update("isDelete", 1);
        UpdateResult updateResult = mailMessageRepository.mongoTemplate().updateMulti(query, update, MailMessage.class);
        return updateResult.getModifiedCount();
    }

    @Override
    public boolean unSubscribe(SubscribeDTO dto) {
        MailSubscription subscription = mailSubscriptionRepository.findFirstByAddress(dto.getAddress());
        if (subscription != null && subscription.getStatus() == 2) {
            throw new BusinessException(MessageStatusCode.ALREADY_UNSUBSCRIBED);
        }
        if (subscription == null) {
            subscription = new MailSubscription();
            subscription.setStatus(2);
            subscription.setAddress(dto.getAddress());
        }
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        MailSubscriptionLog mailSubscriptionLog = new MailSubscriptionLog();
        mailSubscriptionLog.setAddress(dto.getAddress());
        mailSubscriptionLog.setOperation(MailSubEnum.UNSUBSCRIBE.getValue());
        processBaseData(request, mailSubscriptionLog);
        mailSubscriptionLog.setReason(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(dto.getReason())));
        mailSubscriptionLog.setRemark(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(dto.getRemark())));

        mailSubscriptionRepository.save(subscription);
        mailSubscriptionLogRepository.save(mailSubscriptionLog);

        return true;
    }

    private void processBaseData(HttpServletRequest request, MailSubscriptionLog mailSubscriptionLog) {
        mailSubscriptionLog.setIp(ServletUtil.getClientIP(request, (String) null));
        mailSubscriptionLog.setUserAgent(ServletUtil.getHeader(request, "User-Agent", "utf-8"));
        mailSubscriptionLog.setReferer(ServletUtil.getHeader(request, "Referer", "utf-8"));
        UserAgent agent = UserAgentUtil.parse(mailSubscriptionLog.getUserAgent());
        mailSubscriptionLog.setBrowser(agent.getBrowser().getName());
        mailSubscriptionLog.setBrowserVersion(agent.getVersion());
        mailSubscriptionLog.setPlatform(agent.getPlatform().getName());
        mailSubscriptionLog.setOs(agent.getOs().getName());
        mailSubscriptionLog.setOsVersion(agent.getOsVersion());
        mailSubscriptionLog.setEngine(agent.getEngine().getName());
        mailSubscriptionLog.setEngineVersion(agent.getEngineVersion());
        mailSubscriptionLog.setMobile(agent.isMobile());
    }

    @Override
    public Page<MailSubscriptionLog> unSubscribeLog(UnsubscribeLogPageReq req) {
        return mailSubscriptionLogRepository.page(req.getAddress(), req.getBegin(), req.getEnd(), req.getPage(), req.getLimit());
    }

    @Override
    public Page<MailSubscription> unSubscribeList(UnsubscribePageReq req) {
        return mailSubscriptionRepository.page(req.getAddress(), req.getStatus(), req.getBegin(), req.getEnd(), req.getPage(), req.getLimit());
    }

    @Override
    public void subScribe(SubscribeDTO dto) {
        MailSubscription subscription = mailSubscriptionRepository.findFirstByAddress(dto.getAddress());
        if (subscription != null && subscription.getStatus() == 2) {
            subscription.setStatus(1);
            mailSubscriptionRepository.save(subscription);

            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            MailSubscriptionLog mailSubscriptionLog = new MailSubscriptionLog();
            mailSubscriptionLog.setAddress(dto.getAddress());
            mailSubscriptionLog.setOperation(MailSubEnum.SUBSCRIBE.getValue());
            processBaseData(request, mailSubscriptionLog);

            mailSubscriptionLogRepository.save(mailSubscriptionLog);
        }
    }

    @Override
    public List<MailSubscription> listMailSubscriptionsByAddressAndStatus(List<String> addresses, Integer status) {
        return mailSubscriptionRepository.findByAddressInAndStatus(addresses, status);
    }

    @Override
    public void updateTaskLogStatus(String id, MessageSendEnum status, String[] toUser) {
        if (StrUtil.isNotEmpty(id)) {
            log.info("email 更新任务状态为:{} 用户:{} taskId:{}", status, toUser, id);
            UserTaskMonitorDTO dto = new UserTaskMonitorDTO();
            dto.setTaskId(id);
            dto.setStatus(status.getStatus());
            dto.setChannel("email");
            dto.setSendTo(Arrays.stream(toUser).findFirst().get());
            publishFeignClient.updateUserState(dto);
        }
    }
}
