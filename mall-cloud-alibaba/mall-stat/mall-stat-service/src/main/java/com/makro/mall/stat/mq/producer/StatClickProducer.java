package com.makro.mall.stat.mq.producer;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.makro.mall.common.constants.PulsarConstants;
import com.makro.mall.common.enums.LogEventTypeEnum;
import com.makro.mall.common.web.util.IpUtil;
import com.makro.mall.pulsar.producer.PulsarTemplate;
import com.makro.mall.stat.component.StatDelayCache;
import com.makro.mall.stat.pojo.dto.AppUvRequest;
import com.makro.mall.stat.pojo.dto.ClickRequest;
import com.makro.mall.stat.pojo.dto.GoodsClickRequest;
import com.makro.mall.stat.pojo.dto.PageViewRequest;
import com.makro.mall.stat.pojo.entity.LogRecord;
import com.makro.mall.stat.pojo.entity.PageStayRecord;
import com.makro.mall.stat.util.DeviceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 邮件消息生产者
 * @date 2021/11/10
 */
@Service
@Slf4j
public class StatClickProducer implements InitializingBean {

    @Resource
    private PulsarTemplate<LogRecord> pulsarTemplate;

    @Resource
    private StatDelayCache statDelayCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        statDelayCache.register(this::batchSendPageStay, PageStayRecord.class);
    }

    public String sendClickRequest(ClickRequest clickRequest) {
        return send(PulsarConstants.TOPIC_STAT_CLICK, null, clickRequest.getUuid(), clickRequest.generateBizId(), clickRequest, LogEventTypeEnum.GOODS_CLICK_TYPE, clickRequest.getTs() == null ? System.currentTimeMillis() : clickRequest.getTs(), clickRequest.getMemberNo());
    }

    public String sendGoodsClickRequest(GoodsClickRequest goodsClickRequest) {
        return send(PulsarConstants.TOPIC_STAT_GOODS_CLICK, null, goodsClickRequest.getUuid(), goodsClickRequest.generateBizId(), goodsClickRequest, LogEventTypeEnum.GOODS_CLICK_TYPE, goodsClickRequest.getTs() == null ? System.currentTimeMillis() : goodsClickRequest.getTs(), goodsClickRequest.getMemberNo());
    }

    public String sendPageView(PageViewRequest pageViewRequest) {
        return send(PulsarConstants.TOPIC_STAT_PAGE_VIEW, null, pageViewRequest.getUuid(), pageViewRequest.generateBizId(), pageViewRequest, LogEventTypeEnum.PAGE_VIEW_TYPE, pageViewRequest.getTs() == null ? System.currentTimeMillis() : pageViewRequest.getTs(), pageViewRequest.getMemberNo());
    }

    public String sendAppUv(AppUvRequest appUvRequest) {
        return send(PulsarConstants.TOPIC_STAT_APP_UV, appUvRequest.getChannel(), appUvRequest.getUuid(), null, appUvRequest, LogEventTypeEnum.APP_UV_TYPE, appUvRequest.getTs() == null ? System.currentTimeMillis() : appUvRequest.getTs(), appUvRequest.getMemberNo());
    }

    public void delaySendPageStay(List<PageStayRecord> pageStayRecords) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = ServletUtil.getClientIP(request, (String) null);
        try {
            ip = IpUtil.convertIPv6ToIPv4(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String userAgent = ServletUtil.getHeader(request, "User-Agent", "utf-8");
        String referer = ServletUtil.getHeader(request, "Referer", "utf-8");
        String finalIp = ip;
        List<PageStayRecord> logRecords = pageStayRecords.stream().filter(r -> r.getStayTime() < 3600000L
                && StrUtil.isNotEmpty(r.getChannel())).peek(r -> {
            r.setIp(finalIp);
            r.setUserAgent(userAgent);
            r.setReferer(referer);
            r.setTs(r.getTs() == null ? System.currentTimeMillis() : r.getTs());
        }).collect(Collectors.toList());
        statDelayCache.process(logRecords);
    }

    public Boolean batchSendPageStay(List<PageStayRecord> records) {
        Map<String, PageStayRecord> map = new HashMap<String, PageStayRecord>();
        records.forEach(r -> {
            PageStayRecord record = map.putIfAbsent(r.getBizId() + r.getMmCode() + r.getPageNo(), r);
            if (record != null) {
                record.addStayTime(r.getStayTime());
            }
        });
        log.info("batchSendPageStay records:{}", records);
        log.info("batchSendPageStay map:{}", map);
        map.values().forEach(r -> {
            LogRecord record = null;
            try {
                record = LogRecord.builder()
                        .event(LogEventTypeEnum.PAGE_STAY_TYPE.getName())
                        .ip(IpUtil.convertIPv6ToIPv4(r.getIp()))
                        .userAgent(r.getUserAgent())
                        .referer(r.getReferer())
                        .ts(r.getTs())
                        .bizId(r.getBizId())
                        .data(r)
                        .uuid(r.getUuid())
                        .build();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            log.info("batchSendPageStay record:{}", record);
            pulsarTemplate.sendAsync(PulsarConstants.TOPIC_STAT_PAGE_STAY, record);
        });
        return true;
    }

    private String send(String topic, String channel, String uuid, String bizId, Object data, LogEventTypeEnum eventType, Long ts, String memberNo) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = ServletUtil.getClientIP(request, (String) null);
        try {
            ip = IpUtil.convertIPv6ToIPv4(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        LogRecord record = LogRecord.builder()
                .event(eventType.getName())
                .ip(ip)
                .userAgent(ServletUtil.getHeader(request, "User-Agent", "utf-8"))
                .referer(ServletUtil.getHeader(request, "Referer", "utf-8"))
                .ts(ts)
                .bizId(bizId)
                .data(data)
                .build();

        // 生成设备id
        record.setUuid(StrUtil.isBlank(uuid) ? generateDeviceId(record, memberNo) : uuid);
        //如果不为app请求uv则直接返回
        if ((!StrUtil.equals(channel, "app")) && ObjectUtil.equal(eventType, LogEventTypeEnum.APP_UV_TYPE)) {
            return record.getUuid();
        }

        pulsarTemplate.sendAsync(topic, record);
        return record.getUuid();
    }

    private String generateDeviceId(LogRecord input, String memberNo) {
        Map<String, String> map = new TreeMap<>();
        map.put("ip", input.getIp());
        map.put("event", input.getEvent());
        map.put("userAgent", input.getUserAgent());
        if (StringUtils.isNotEmpty(memberNo)) {
            map.put("memberNo", memberNo);
        }
        return DeviceUtil.generateWebUniqueDeviceId(map);
    }


}
