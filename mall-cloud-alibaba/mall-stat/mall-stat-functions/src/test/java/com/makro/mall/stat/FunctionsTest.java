package com.makro.mall.stat;

import com.makro.mall.stat.ods.StatClickFunction;
import org.apache.pulsar.common.functions.FunctionConfig;
import org.apache.pulsar.functions.LocalRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/28
 */
public class FunctionsTest {

    public static void main(String[] args) throws Exception {

        // multiple topics
        Collection<String> inputTopics = new ArrayList<String>();
        inputTopics.add("persistent://public/default/mail_stat_goods_click");
        inputTopics.add("persistent://public/default/mail_stat_page_view");
        inputTopics.add("persistent://public/default/mail_stat_page_stay");
        inputTopics.add("persistent://public/default/mail_stat_app_uv");

        Map<String, Object> userConfig = new HashMap<String, Object>();
        userConfig.put("publish-goods-click-sink-topic", "persistent://public/default/mail_stat_goods_click_sink");
        userConfig.put("publish-page-view-sink-topic", "persistent://public/default/mail_stat_page_view_sink");
        userConfig.put("publish-page-stay-sink-topic", "persistent://public/default/mail_stat_page_stay_sink");
        userConfig.put("publish-app-uv-sink-topic", "persistent://public/default/mail_stat_app_uv_sink");
        FunctionConfig functionConfig = FunctionConfig.builder()
                .className(StatClickFunction.class.getName())
                .inputs(inputTopics)
                .userConfig(userConfig)
                .name("stat-click-functions")
                .namespace("default")
                .tenant("public")
                .runtime(FunctionConfig.Runtime.JAVA)
                .autoAck(true)
                .build();

        /**
         *
         *
         *  .clientAuthParams("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik5rRXdSVVU1TUVOQlJrWTJNalEzTVRZek9FVkZRVVUyT0RNME5qUkRRVEU1T1VNMU16STVPUSJ9.eyJodHRwczovL3N0cmVhbW5hdGl2ZS5pby91c2VybmFtZSI6InRzcGFubkBzbmRlbW8uYXV0aC5zdHJlYW1uYXRpdmUuY2xvdWQiLCJpc3MiOiJodHRwczovL2F1dGguc3RyZWFtbmF0aXZlLmNsb3VkLyIsInN1YiI6IkNJRzFGTlNmRnNiOXMyc2lSRHNoY0xCZE1zNWtEMUxzQGNsaWVudHMiLCJhdWQiOiJ1cm46c246cHVsc2FyOnNuZGVtbzpkZW1vLWNsdXN0ZXIiLCJpYXQiOjE2NDk5MzQ4MjksImV4cCI6MTY1MDUzOTYyOSwiYXpwIjoiQ0lHMUZOU2ZGc2I5czJzaVJEc2hjTEJkTXM1a0QxTHMiLCJzY29wZSI6ImFkbWluIGFjY2VzcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsInBlcm1pc3Npb25zIjpbImFkbWluIiwiYWNjZXNzIl19.hXmqplmhxVD-hFrtEJSJSOgNDq0dwxFntfz4PXNsKrDBQZe2gxPfztR13cdpeDJzlza_rCOLfFCqCqzcnV1DJFnoSpsVNjygBrHkiTXKxufDuyvvReLYDujtMuDUl5mr-y5IlKpeiUEIbk3RaW_AVVP6XCzxxqlXNQBq8OLJ3IpYFowM1TRjgEIvHw-K9f-j_BxNDaFTzi1VE40_zUZ1mDA2u9QFre-zhJ84j_Wjj-qdXWygd4BI2lj4MEtK6FODVQVab_cStHYNtx7mA7wk2KsPkXoJ1qWLrL8NjlS7YjLd69vJQTyAkGqzTgr3K5R3TIOinA6KG6ZzJqQmdZhBKQ")
         */
        LocalRunner localRunner = LocalRunner.builder()
                //.brokerServiceUrl("pulsar+ssl://demo.sndemo.snio.cloud:6651")
                //.clientAuthPlugin("org.apache.pulsar.client.impl.auth.AuthenticationToken")
                //.clientAuthParams("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik5rRXdSVVU1TUVOQlJrWTJNalEzTVRZek9FVkZRVVUyT0RNME5qUkRRVEU1T1VNMU16STVPUSJ9.eyJodHRwczovL3N0cmVhbW5hdGl2ZS5pby91c2VybmFtZSI6InRzcGFubkBzbmRlbW8uYXV0aC5zdHJlYW1uYXRpdmUuY2xvdWQiLCJpc3MiOiJodHRwczovL2F1dGguc3RyZWFtbmF0aXZlLmNsb3VkLyIsInN1YiI6IkNJRzFGTlNmRnNiOXMyc2lSRHNoY0xCZE1zNWtEMUxzQGNsaWVudHMiLCJhdWQiOiJ1cm46c246cHVsc2FyOnNuZGVtbzpkZW1vLWNsdXN0ZXIiLCJpYXQiOjE2NDk5MzQ4MjksImV4cCI6MTY1MDUzOTYyOSwiYXpwIjoiQ0lHMUZOU2ZGc2I5czJzaVJEc2hjTEJkTXM1a0QxTHMiLCJzY29wZSI6ImFkbWluIGFjY2VzcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsInBlcm1pc3Npb25zIjpbImFkbWluIiwiYWNjZXNzIl19.hXmqplmhxVD-hFrtEJSJSOgNDq0dwxFntfz4PXNsKrDBQZe2gxPfztR13cdpeDJzlza_rCOLfFCqCqzcnV1DJFnoSpsVNjygBrHkiTXKxufDuyvvReLYDujtMuDUl5mr-y5IlKpeiUEIbk3RaW_AVVP6XCzxxqlXNQBq8OLJ3IpYFowM1TRjgEIvHw-K9f-j_BxNDaFTzi1VE40_zUZ1mDA2u9QFre-zhJ84j_Wjj-qdXWygd4BI2lj4MEtK6FODVQVab_cStHYNtx7mA7wk2KsPkXoJ1qWLrL8NjlS7YjLd69vJQTyAkGqzTgr3K5R3TIOinA6KG6ZzJqQmdZhBKQ")
                .brokerServiceUrl("pulsar://10.58.5.152:6650")
                .stateStorageServiceUrl("bk://10.58.5.152:4181")
                .functionConfig(functionConfig)
                .build();

        localRunner.start(false);

        Thread.sleep(3000000);
        localRunner.stop();
        System.exit(0);
    }

}
