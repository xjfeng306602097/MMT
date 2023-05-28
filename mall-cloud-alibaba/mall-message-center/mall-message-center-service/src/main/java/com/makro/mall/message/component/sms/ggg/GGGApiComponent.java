package com.makro.mall.message.component.sms.ggg;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MessageStatusCode;
import com.makro.mall.message.pojo.dto.GGGSmsMultipleResDTO;
import com.makro.mall.message.pojo.entity.MessageProperties;
import com.makro.mall.message.service.MessagePropertiesService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/21
 */
@Component
@Slf4j
public class GGGApiComponent {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private MessagePropertiesService messagePropertiesService;

    public String generateTransactionId() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return DTF.format(localDateTime) + (int) ((Math.random() * 9 + 1) * 100000);
    }

    public String generateJobId() {
        return UuidUtils.generateUuid();
    }

    public String md5(String content) {
        return MD5.create().digestHex(content);
    }

    public String session(String content) {
        return md5(content);
    }

    public List<GGGSmsMultipleResDTO> sendMultipleSms(JSONObject req) {
        MessageProperties properties = messagePropertiesService.findFirstById("1");
        JSONObject config = properties.getSmsMap().get(properties.getSmsChannel().getValue());
        String body = HttpRequest.post(config.getString("smsMultipleUrl"))
                .body(req.toString())
                .header("Content-Type", "application/json")
                .header("sisession", session(req.toJSONString()))
                .timeout(20000)
                .execute().body();
        log.info("sendMultipleSms req:[{}] body:[{}]", req, body);
        if (!body.startsWith("[")) {
            body = "[" + body + "]";
        }
        return JSON.parseArray(body, GGGSmsMultipleResDTO.class);
    }

    @SneakyThrows
    public JSONObject generateMultipleReq(String jobId, List<String> receivers, String msg) {
        MessageProperties properties = messagePropertiesService.findFirstById("1");
        JSONObject config = properties.getSmsMap().get(properties.getSmsChannel().getValue());
        Assert.isTrue(CollectionUtil.isNotEmpty(receivers), MessageStatusCode.HAVE_NO_RECEIVERS);
        JSONObject req = new JSONObject();
        req.put("job_id", jobId);
        JSONArray array = new JSONArray();
        for (String receiver : receivers) {
            JSONObject item = new JSONObject();
            item.put("transaction_id", generateTransactionId());
            item.put("msisdn", receiver);
            array.add(item);
        }
        req.put("to", array);
        req.put("sender", config.getString("smsSender"));
        req.put("msg", URLEncoder.encode(msg, StandardCharsets.UTF_8));
        req.put("project_id", config.getString("smsProjectId"));
        req.put("pwd", config.getString("smsPwd"));
        return req;
    }

}
