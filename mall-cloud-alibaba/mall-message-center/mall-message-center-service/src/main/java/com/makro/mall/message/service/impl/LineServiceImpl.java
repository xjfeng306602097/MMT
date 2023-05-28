package com.makro.mall.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MessageStatusCode;
import com.makro.mall.message.dto.ChatQueueDTO;
import com.makro.mall.message.enums.LineReceiveEnum;
import com.makro.mall.message.mq.producer.BindingLineUserProducer;
import com.makro.mall.message.mq.producer.LineProducer;
import com.makro.mall.message.pojo.dto.BindingLineUserDTO;
import com.makro.mall.message.pojo.entity.LineReceiveMessage;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.repository.LineReceiveMessageRepository;
import com.makro.mall.message.repository.LineSendMessageRepository;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.message.service.LineService;
import com.makro.mall.message.vo.MessagePageReqVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineReceiveMessageRepository lineReceiveMessageRepository;
    private final LineSendMessageRepository lineSendMessageRepository;
    private final LineSDK lineSDK;
    private final MmActivityFeignClient mmActivityFeignClient;
    private final BindingLineUserProducer bindingLineUserProducer;
    private final LineProducer lineProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .scheme("https")
                .path(path).build()
                .toUri();
    }


    @Override
    public void saveReceive(Event event) {
        if (event instanceof MessageEvent) {
            saveMessageContent((MessageEvent<MessageContent>) event);
        } else {
            saveMessageContent(event);
        }
    }

    @Override
    public void saveSend(String type, Message message, Set<LineSendMessage.LineCustomer> to) {
        LineSendMessage lineSendMessage = new LineSendMessage();
        lineSendMessage.setType(type);
        lineSendMessage.setMessage(message);
        lineSendMessage.setTo(to);
        lineSendMessageRepository.save(lineSendMessage);

        if (message instanceof TextMessage) {
            demoSave(to.stream().findFirst().get().getLineId(), ((TextMessage) message).getText(), false);
        }
    }

    @Override
    public void demoSave(String key, String message, Boolean isUser) {
        synchronized (LineServiceImpl.class) {
            chatMemberhandle(key);


            ChatQueueDTO dto = new ChatQueueDTO();
            dto.setMessage(message);
            dto.setIsUser(isUser);
            chatQueuehandle(key, dto);
        }
    }

    private void chatQueuehandle(String key, ChatQueueDTO dto) {
        redisTemplate.opsForList().rightPush("demo:chatQueue:" + key, dto);
        //Long rightPush = redisTemplate.opsForList().rightPush("demo:chatMember:"+key, dto);
        //dto.setIndex(rightPush);
        //redisTemplate.opsForList().set(key, rightPush - 1, dto);

    }

    private void chatMemberhandle(String key) {
        redisTemplate.opsForList().remove("demo:chatMember", 0, key);
        redisTemplate.opsForList().leftPush("demo:chatMember", key);
    }

    @Override
    public Object verify(String code, String state, String mmCode, String pageNo) {
        String token = lineSDK.token(code, mmCode);
        String lineUserId = lineSDK.verify(token);
        String mailUrl = getMailUrl(lineUserId, null, state, mmCode, pageNo);
        return new VerifyDTO(mailUrl);
    }

    @Override
    public Boolean multicastFlex(LineSendMessage lineSendMessage) {
        lineSendMessageRepository.save(lineSendMessage);
        lineProducer.multicastFlex(lineSendMessage, 0L);
        return true;
    }

    @Override
    public String getMailUrl(String sub, String email, String state, String mmCode, String pageNo) {
        Assert.notNull(ObjectUtil.isNotNull(mmCode), MessageStatusCode.MMCODE_IS_NOT_NULL);
        MmActivity activity = mmActivityFeignClient.getPublishUrlByCode(mmCode).getData();
        //根据sub获取用户id

        String s = activity.getPublishUrl()
                + "?q=line"
                + "&c=" + state
                + "&p=" + pageNo
                + "&s=" + activity.getStoreCode();
        //群发不校验 不绑定
        if (StrUtil.isNotBlank(state) && !StrUtil.equals(state, "0")) {
            bindingLineUserProducer.bindingLineUser(new BindingLineUserDTO(state, sub));
        }
        log.info("getMailUrl return lineId:{} email:{} state:{} mmCode:{} return:{}", sub, email, state, mmCode, s);
        return s;
    }

    @Override
    public Page<LineSendMessage> page(MessagePageReqVO vo) {
        return lineSendMessageRepository.page(vo);
    }

    @Override
    public JSONObject search(String key) {
        String message = "{\n" +
                "    \"q\": \"${key}\",\n" +
                "    \"page\": 1,\n" +
                "    \"size\": 1,\n" +
                "    \"querySuggestions\": false,\n" +
                "    \"filters\": {\n" +
                "        \"storeCode\": \"17\"\n" +
                "    }\n" +
                "}";
        String replace = message.replace("${key}", key);
        String body = HttpRequest.post("https://search.maknet.siammakro.cloud/search/api/v1/indexes/products/search")
                .header("Content-Type", "application/json")
                .timeout(20000)
                .body(replace)
                .execute().body();
        return JSONObject.parseObject(body).getJSONArray("hits").getJSONObject(0);
    }

    private void saveMessageContent(Event event) {
        LineReceiveMessage lineReceiveMessage = new LineReceiveMessage();
        lineReceiveMessage.setUserId(event.getSource().getUserId());
        lineReceiveMessage.setSenderId(event.getSource().getSenderId());
        lineReceiveMessage.setTimestamp(event.getTimestamp());
        lineReceiveMessage.setMode(event.getMode().toString());
        lineReceiveMessage.setType(LineReceiveEnum.getType(event));
        lineReceiveMessageRepository.save(lineReceiveMessage);
    }

    private <U extends MessageContent> void saveMessageContent(MessageEvent<U> event) {
        LineReceiveMessage lineReceiveMessage = new LineReceiveMessage();
        lineReceiveMessage.setReplyToken(event.getReplyToken());
        lineReceiveMessage.setUserId(event.getSource().getUserId());
        lineReceiveMessage.setSenderId(event.getSource().getSenderId());
        lineReceiveMessage.setMessage((JSONObject) JSON.toJSON(event.getMessage()));
        lineReceiveMessage.setTimestamp(event.getTimestamp());
        lineReceiveMessage.setMode(event.getMode().toString());
        lineReceiveMessage.setType(LineReceiveEnum.getLineMessageType(event.getMessage()));
        lineReceiveMessageRepository.save(lineReceiveMessage);

        if (event.getMessage() instanceof TextMessageContent) {
            demoSave(lineReceiveMessage.getUserId(), ((TextMessageContent) event.getMessage()).getText(), true);
        }
    }

    @Data
    private static class VerifyDTO {
        private String url;

        public VerifyDTO(String url) {
            this.url = url;
        }
    }
}
