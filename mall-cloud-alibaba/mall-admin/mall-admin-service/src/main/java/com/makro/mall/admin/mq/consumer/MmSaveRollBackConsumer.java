package com.makro.mall.admin.mq.consumer;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmBounceRate;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.admin.service.MmBounceRateService;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.product.api.ProdListFeignClient;
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
public class MmSaveRollBackConsumer {

    private final MmActivityService mmActivityService;
    private final MmBounceRateService mmBounceRateService;
    private final ProdListFeignClient prodListFeignClient;
    private final RedissonClient redissonClient;


    @PulsarConsumer(topic = PulsarConstants.TOPIC_MM_SAVE_ROLL_BACK, subscriptionType = {SubscriptionType.Shared})
    public void consumeMmSaveRollBack(byte[] bytes) {
        String mmCode = new String(bytes);
        try {
            if (redissonClient.getLock("lock:mm:save:rollback:" + mmCode).tryLock(0, 60, TimeUnit.SECONDS)) {
                log.info("保存MM以及商品接口失败,回滚队列接收到消息，mmCode: {}", mmCode);
                mmActivityService.remove(new LambdaUpdateWrapper<MmActivity>().eq(MmActivity::getMmCode, mmCode));
                mmBounceRateService.remove(new LambdaUpdateWrapper<MmBounceRate>().eq(MmBounceRate::getMmCode, mmCode));
                prodListFeignClient.remove(mmCode);
                log.info("保存MM以及商品接口失败,回滚队列处理消息完毕，mmCode: {}", mmCode);
            }
        } catch (Exception e) {
            //异常输出
            log.error("保存MM以及商品接口失败,回滚队列执行失败", e);
        }
    }

}
