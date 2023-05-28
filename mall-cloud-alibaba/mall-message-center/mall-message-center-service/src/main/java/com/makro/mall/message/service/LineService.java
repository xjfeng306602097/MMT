package com.makro.mall.message.service;


import com.alibaba.fastjson2.JSONObject;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.message.Message;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import org.springframework.data.domain.Page;

import java.net.URI;
import java.util.Set;

public interface LineService {

    URI createUri(String s);

    void saveReceive(Event event);

    void saveSend(String type, Message message, Set<LineSendMessage.LineCustomer> to);

    void demoSave(String key, String message, Boolean isUser);

    Object verify(String code, String state, String mmCode, String pageNo);

    Boolean multicastFlex(LineSendMessage lineSendMessage);

    String getMailUrl(String sub, String email, String state, String mmCode, String pageNo);

    Page<LineSendMessage> page(MessagePageReqVO vo);

    JSONObject search(String key);
}
