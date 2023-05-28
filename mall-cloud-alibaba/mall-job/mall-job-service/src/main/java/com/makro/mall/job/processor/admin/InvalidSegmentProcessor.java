package com.makro.mall.job.processor.admin;

import com.makro.mall.admin.api.MmSegmentFeignClient;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/15 MmSegment过期失效定时调度
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvalidSegmentProcessor {

    private final MmSegmentFeignClient mmSegmentFeignClient;

    @XxlJob("invalidSegmentProcessor")
    public Boolean invalidSegmentProcessor() {
       return mmSegmentFeignClient.invalidSegmentHandler().getData();
    }


}
