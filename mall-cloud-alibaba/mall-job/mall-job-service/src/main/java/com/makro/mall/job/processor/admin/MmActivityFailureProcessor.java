package com.makro.mall.job.processor.admin;

import com.makro.mall.admin.api.MmActivityFeignClient;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author jincheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MmActivityFailureProcessor {
    private final MmActivityFeignClient activityFeignClient;

    @XxlJob("mmActivityFailureProcessor")
    public Boolean mmActivityFailureProcessor() {
       return activityFeignClient.scanMmActivityForFailure().getData();
    }
}
