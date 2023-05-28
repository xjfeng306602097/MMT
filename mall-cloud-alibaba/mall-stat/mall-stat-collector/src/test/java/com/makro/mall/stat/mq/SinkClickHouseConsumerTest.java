package com.makro.mall.stat.mq;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.stat.CustomSpringbootTest;
import com.makro.mall.stat.mq.consumer.SinkClickHouseConsumer;
import com.makro.mall.stat.pojo.entity.PageViewRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/5/27
 */
@CustomSpringbootTest
public class SinkClickHouseConsumerTest {

    @Autowired
    private SinkClickHouseConsumer sinkClickHouseConsumer;

    @Test
    @DisplayName("消费消息测试")
    public void setSinkClickHouseConsumer() {
        JSONObject json = JSONObject.parse("{\"ip\":\"94.74.106.250\",\"ts\":1684931314340,\"event\":\"page_view\",\"uuid\":\"a0db587d3564a92a19f16cff40dad657\",\"bizId\":\"47291b25-ec1e-4d04-8ff7-3008575bbde6\",\"userAgent\":\"Mozilla/5.0 (Linux; Android 13; 22011211C Build/TP1A.220624.014; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/110.0.5481.153 Mobile Safari/537.36 Line/12.12.0 LIFF\",\"mobile\":1,\"browser\":\"Chrome\",\"browserVersion\":\"110.0.5481.153\",\"platform\":\"Android\",\"os\":\"Android\",\"osVersion\":\"13\",\"engine\":\"Webkit\",\"engineVersion\":\"537.36\",\"referer\":\"uat-file.makromail.atmakro.com\",\"totalCount\":3,\"memberNo\":\"5P2th5kiPjCy7ASJQrYsSJ\",\"mmCode\":\"MM-c15daa47-d569-4e0a-bc69-6d856bc1fe0c\",\"storeCode\":\"21\",\"channel\":\"line\",\"pageNo\":\"1\",\"pageUrl\":\"https://uat-file.makromail.atmakro.com/makro-htmls/mm/MM-c15daa47-d569-4e0a-bc69-6d856bc1fe0c.html?q=line&c=5P2th5kiPjCy7ASJQrYsSJ&p=1&s=21&mm=MM-c15daa47-d569-4e0a-bc69-6d856bc1fe0c&sub=U32c2fa997d4f426699ec7a8a9a13afda\",\"isNew\":0}");
        PageViewRecord record = JSON.toJavaObject(json, PageViewRecord.class);
        sinkClickHouseConsumer.consumePageView(record);
    }


}
