package com.makro.mall.stat.mq.consumer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.common.util.AesBase62Util;
import com.makro.mall.pulsar.annotation.PulsarConsumer;
import com.makro.mall.stat.pojo.entity.AppUvRecord;
import com.makro.mall.stat.pojo.entity.GoodClickRecord;
import com.makro.mall.stat.pojo.entity.PageStayRecord;
import com.makro.mall.stat.pojo.entity.PageViewRecord;
import com.makro.mall.stat.pojo.metadata.AppUvLog;
import com.makro.mall.stat.pojo.metadata.GoodsClickLog;
import com.makro.mall.stat.pojo.metadata.PageStayLog;
import com.makro.mall.stat.pojo.metadata.PageViewLog;
import com.makro.mall.stat.service.AppUvLogService;
import com.makro.mall.stat.service.GoodsClickLogService;
import com.makro.mall.stat.service.PageStayLogService;
import com.makro.mall.stat.service.PageViewLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description clickhouse sink consumer for pulsar
 * @date 2022/7/7
 * @date 2022/9/7 补充会员类型
 */
@Component
@Slf4j
public class SinkClickHouseConsumer {

    @Resource
    private GoodsClickLogService goodsClickLogService;
    @Resource
    private PageViewLogService pageViewLogService;
    @Resource
    private PageStayLogService pageStayLogService;
    @Resource
    private AppUvLogService appUvLogService;
    @Resource
    private CustomerFeignClient customerFeignClient;
    public final LoadingCache<MmCustomer, MmCustomerVO> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .concurrencyLevel(8)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .removalListener((RemovalListener<MmCustomer, MmCustomerVO>) notification -> {
                log.info(notification.getKey() + " was removed, cause is " + notification.getCause());
            })
            .build(new CacheLoader<>() {
                @Override
                public MmCustomerVO load(@NotNull MmCustomer key) {
                    return customerFeignClient.getVO(key).getData();
                }
            });

    @PulsarConsumer(topic = PulsarConstants.TOPIC_MAIL_STAT_GOODS_CLICK_SINK, clazz = GoodClickRecord.class, subscriptionType = {SubscriptionType.Failover})
    public void consumeGoodsClick(GoodClickRecord record) {
        try {
            log.info("consumeGoodsClick 接收到消息，record{}", JSON.toJSONString(record));
            GoodsClickLog goodsClickLog = new GoodsClickLog();
            BeanUtil.copyProperties(record, goodsClickLog);
            //如果渠道是APP则不解密
            boolean app = StrUtil.equals("app", record.getChannel());
            goodsClickLog.setMemberNo(app ? goodsClickLog.getMemberNo() : AesBase62Util.decode(goodsClickLog.getMemberNo()));
            if (StrUtil.isNotBlank(goodsClickLog.getMemberNo())) {
                MmCustomer mmCustomer = app ? new MmCustomer().setCustomerCode(goodsClickLog.getMemberNo()) : new MmCustomer().setId(Long.valueOf(goodsClickLog.getMemberNo()));
                MmCustomerVO customerVO = CACHE.get(mmCustomer);
                if (ObjectUtil.isNotNull(customerVO)) {
                    if (ObjectUtil.isNotNull(customerVO.getId())) {
                        goodsClickLog.setMemberNo(String.valueOf(customerVO.getId()));
                    }
                    if (CollUtil.isNotEmpty(customerVO.getMemberTypes())) {
                        goodsClickLog.setMemberType(StrUtil.join(",", customerVO.getMemberTypes().stream().map(MmMemberType::getId).collect(Collectors.toList())));

                    }
                }
            }
            goodsClickLogService.add(goodsClickLog);
            log.info("consumeGoodsClick 处理消息完毕，goodsClickLog{}", goodsClickLog);
        } catch (Exception e) {
            log.error("consumeGoodsClick 处理消息异常，record{} e{}", JSON.toJSONString(record), e);
        }
    }

    @PulsarConsumer(topic = PulsarConstants.TOPIC_MAIL_STAT_PAGE_VIEW_SINK, clazz = PageViewRecord.class, subscriptionType = {SubscriptionType.Failover})
    public void consumePageView(PageViewRecord record) {
        try {
            log.info("consumePageView 接收到消息，record{}", JSON.toJSONString(record));
            PageViewLog pageViewLog = new PageViewLog();
            BeanUtil.copyProperties(record, pageViewLog);
            //如果渠道是APP则不解密
            boolean app = StrUtil.equals("app", record.getChannel());
            pageViewLog.setMemberNo(app ? pageViewLog.getMemberNo() : AesBase62Util.decode(pageViewLog.getMemberNo()));
            if (StrUtil.isNotBlank(pageViewLog.getMemberNo())) {
                MmCustomer mmCustomer = app ? new MmCustomer().setCustomerCode(pageViewLog.getMemberNo()) : new MmCustomer().setId(Long.valueOf(pageViewLog.getMemberNo()));
                MmCustomerVO customerVO = CACHE.get(mmCustomer);
                if (ObjectUtil.isNotNull(customerVO)) {
                    if (ObjectUtil.isNotNull(customerVO.getId())) {
                        pageViewLog.setMemberNo(String.valueOf(customerVO.getId()));
                    }
                    if (CollUtil.isNotEmpty(customerVO.getMemberTypes())) {
                        pageViewLog.setMemberType(StrUtil.join(",", customerVO.getMemberTypes().stream().map(MmMemberType::getId).collect(Collectors.toList())));
                    }
                }
            }
            pageViewLogService.add(pageViewLog);
            log.info("consumePageView 处理消息完毕，pageViewLog{}", pageViewLog);
        } catch (Exception e) {
            log.error("consumePageView 处理消息异常，record{} e{}", JSON.toJSONString(record), e);
        }
    }

    @PulsarConsumer(topic = PulsarConstants.TOPIC_MAIL_STAT_PAGE_STAY_SINK, clazz = PageStayRecord.class, subscriptionType = {SubscriptionType.Failover})
    public void consumePageStay(PageStayRecord record) {
        try {
            log.info("consumePageStay 接收到消息，record{}", JSON.toJSONString(record));
            PageStayLog pageStayLog = new PageStayLog();
            BeanUtil.copyProperties(record, pageStayLog);
            //如果渠道是APP则不解密
            boolean app = StrUtil.equals("app", record.getChannel());
            pageStayLog.setMemberNo(app ? pageStayLog.getMemberNo() : AesBase62Util.decode(pageStayLog.getMemberNo()));
            if (StrUtil.isNotBlank(pageStayLog.getMemberNo())) {
                MmCustomer mmCustomer = app ? new MmCustomer().setCustomerCode(pageStayLog.getMemberNo()) : new MmCustomer().setId(Long.valueOf(pageStayLog.getMemberNo()));
                MmCustomerVO customerVO = CACHE.get(mmCustomer);
                if (ObjectUtil.isNotNull(customerVO)) {
                    if (ObjectUtil.isNotNull(customerVO.getId())) {
                        pageStayLog.setMemberNo(String.valueOf(customerVO.getId()));
                    }
                    if (CollUtil.isNotEmpty(customerVO.getMemberTypes())) {
                        pageStayLog.setMemberType(StrUtil.join(",", customerVO.getMemberTypes().stream().map(MmMemberType::getId).collect(Collectors.toList())));
                    }
                }
            }
            pageStayLogService.save(pageStayLog);
            log.info("consumePageStay 处理消息完毕，pageStayLog{}", pageStayLog);
        } catch (Exception e) {
            log.error("consumePageStay 处理消息异常，record{} e{}", JSON.toJSONString(record), e);
        }
    }

    @PulsarConsumer(topic = PulsarConstants.TOPIC_MAIL_STAT_APP_UV_SINK, clazz = AppUvRecord.class, subscriptionType = {SubscriptionType.Failover})
    public void consumeAppUv(AppUvRecord record) {
        try {
            log.info("consumeAppUv 接收到消息，record{}", JSON.toJSONString(record));
            AppUvLog appUvLog = new AppUvLog();
            BeanUtil.copyProperties(record, appUvLog);
            //渠道只能是APP
            if (StrUtil.isNotBlank(record.getMemberNo())) {
                MmCustomerVO customerVO = CACHE.get(new MmCustomer().setCustomerCode(appUvLog.getMemberNo()));
                if (ObjectUtil.isNotNull(customerVO)) {
                    if (ObjectUtil.isNotNull(customerVO.getId())) {
                        appUvLog.setMemberNo(String.valueOf(customerVO.getId()));
                    }
                    if (CollUtil.isNotEmpty(customerVO.getMemberTypes())) {
                        appUvLog.setMemberType(StrUtil.join(",", customerVO.getMemberTypes().stream().map(MmMemberType::getId).collect(Collectors.toList())));
                    }
                }
            }
            appUvLogService.save(appUvLog);
            log.info("consumeAppUv 处理消息完毕，appUvLog{}", appUvLog);
        } catch (Exception e) {
            log.error("consumeAppUv 处理消息异常，record{} e{}", JSON.toJSONString(record), e);
        }
    }

}
