package com.makro.mall.message.component.sms.ggg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.message.component.sms.SmsSenderChain;
import com.makro.mall.message.enums.SmsChannelEnum;
import com.makro.mall.message.pojo.dto.GGGSmsMultipleResDTO;
import com.makro.mall.message.pojo.dto.SmsMessageDTO;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaojunfeng
 * @description AWS邮件服务
 * @date 2021/11/4
 */
@Component
@Slf4j
public class GGGsmsSender implements SmsSenderChain {

    private static final String SUCCESS_CODE = "000";
    private static final List<SmsChannelEnum> ENUMS = List.of(SmsChannelEnum.GGG_MULTIPLE);
    @Resource
    private GGGApiComponent gggApiComponent;
    @Resource
    private SmsService smsService;

    @Override
    public boolean accept(SmsMessageDTO dto) {
        return ENUMS.contains(dto.getChannelEnum());
    }

    @Override
    public void send(SmsMessageDTO dto) {
        try {
            String jobId = StrUtil.isNotEmpty(dto.getBizId()) ? dto.getBizId() : gggApiComponent.generateJobId();
            if (dto.getChannelEnum() == SmsChannelEnum.GGG_MULTIPLE) {
                JSONObject req = gggApiComponent.generateMultipleReq(jobId, dto.getReceivers(), dto.getMsg());
                List<GGGSmsMultipleResDTO> body = gggApiComponent.sendMultipleSms(req);
                JSONObject smsContent = new JSONObject();
                smsContent.put("req", req);
                smsContent.put("res", body);
                int failCount = 0;
                for (GGGSmsMultipleResDTO resDTO : body) {
                    if (!Objects.equals(SUCCESS_CODE, resDTO.getCode())) {
                        failCount++;
                    }
                }

                MessageSendEnum status = updateSendLogStatus(dto, body, smsContent, failCount);

                //如果为任务发布则更新任务状态
                smsService.updateTaskUserStatus(dto.getMmPublishJobSmsTaskId(), status, dto.getReceivers());
                smsService.updateTaskStatus(dto.getMmPublishJobSmsTaskId(), status, dto.getReceivers());
            }
        } catch (Exception e) {
            // 发送失败,异常处理,修改消息状态
            log.error("GGGsmsSender.send发送失败", e);
            SmsMessage smsMessage = new SmsMessage();
            BeanUtil.copyProperties(dto, smsMessage);
            smsMessage.setId(dto.getId());
            MessageSendEnum status = MessageSendEnum.FAILED;
            smsMessage.setStatus(status);
            smsService.update(smsMessage);
            smsService.updateTaskStatus(dto.getMmPublishJobSmsTaskId(), MessageSendEnum.FAILED, dto.getReceivers());
        }
    }

    private MessageSendEnum updateSendLogStatus(SmsMessageDTO dto, List<GGGSmsMultipleResDTO> resDTOS, JSONObject smsContent, int failCount) {
        SmsMessage smsMessage = new SmsMessage();
        BeanUtil.copyProperties(dto, smsMessage);
        smsMessage.setId(dto.getId());
        smsMessage.setSmsContent(smsContent);
        MessageSendEnum status = failCount == 0 ? MessageSendEnum.SUCCEEDED : (failCount == resDTOS.size() ? MessageSendEnum.FAILED : MessageSendEnum.PARTIALLY_SUCCEEDED);
        smsMessage.setStatus(status);
        smsService.update(smsMessage);
        return status;
    }


}
