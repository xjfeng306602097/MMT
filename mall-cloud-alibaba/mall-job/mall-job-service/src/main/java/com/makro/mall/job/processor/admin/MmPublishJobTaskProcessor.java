package com.makro.mall.job.processor.admin;

import com.makro.mall.admin.api.PublishFeignClient;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MmPublishJobTaskProcessor {

    private final PublishFeignClient publishFeignClient;


    @XxlJob("scanMmPublishJobTask")
    public Boolean scanMmPublishJobTask() {
        return publishFeignClient.scanMmPublishJobTask().getData();
    }


    @XxlJob("mmPublishJobSmsTaskProcessor")
    public Boolean mmPublishJobSmsTaskProcessor() {
        return publishFeignClient.scanMmPublishJobSmsTask().getData();
    }


    @XxlJob("mmPublishJobLineTaskProcessor")
    public Boolean mmPublishJobLineTaskProcessor() {
        return publishFeignClient.scanMmPublishJobTask().getData();
    }


}
