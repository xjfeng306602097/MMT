package com.makro.mall.admin.mq.consumer;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.service.MmSegmentService;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 功能描述:
 * Mail发布工程
 *
 * @Author: 卢嘉俊
 * @Date: 2022/6/1
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InvalidSegmentConsumer {

    private final MmSegmentService mmSegmentService;
    private final RedissonClient redissonClient;


    @PulsarConsumer(topic = PulsarConstants.TOPIC_INVALID_SEGMENT, clazz = MmSegment.class, subscriptionType = {SubscriptionType.Shared})
    public void consumeInvalidSegment(MmSegment segment) {
        Long id = segment.getId();
        try {
            if (redissonClient.getLock("lock:invalid:segment:" + id).tryLock(0, 60, TimeUnit.SECONDS)) {
                log.info("失效segment队列接收到消息，id: {}", id);
                MmSegment oldSegment = mmSegmentService.getById(id);

                if (oldSegment.getEndTime().isAfter(segment.getEndTime())) {
                    return;
                }
                mmSegmentService.update(new LambdaUpdateWrapper<MmSegment>()
                        .set(MmSegment::getInvalid, 1)
                        .eq(MmSegment::getId, id)
                        .eq(MmSegment::getInvalid, 0));


                log.info("失效segment队列处理消息完毕，MmPublishJobEmailTask: {}", id);
            }
        } catch (Exception e) {
            //异常输出
            log.error("失效segment队列执行失败", e);
        }
    }

}
