package com.makro.mall.message.component;

import com.makro.mall.message.CustomSpringbootTest;
import com.makro.mall.message.component.sms.ggg.GGGApiComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author xiaojunfeng
 * @description 邮件发送器
 * @date 2021/11/3
 */
@CustomSpringbootTest
@Slf4j
public class GGGSmsSenderTest {

    @Resource
    private GGGApiComponent gggApiComponent;


    @Test
    @DisplayName("发送邮件")
    void send() throws InterruptedException {
        //String jobId = gggApiComponent.generateJobId();
        //List<String> receivers = List.of("66613094215", "66613093719");
        //JSONObject req = gggApiComponent.generateMultipleReq(jobId, receivers, "hello kingwa");
        //List<GGGSmsMultipleResDTO> list = gggApiComponent.sendMultipleSms(req);
        //log.info(JSON.toJSONString(list));
    }


}
