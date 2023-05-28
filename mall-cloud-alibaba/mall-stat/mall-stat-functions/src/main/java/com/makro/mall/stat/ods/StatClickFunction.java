package com.makro.mall.stat.ods;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.stat.pojo.entity.*;
import com.makro.mall.stat.util.DeviceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/27
 */
public class StatClickFunction implements Function<LogRecord, Void> {

    public static final String PERSISTENT_PUBLIC_DEFAULT_GOODS_CLICK_SINK = "persistent://public/default/mail_stat_goods_click_sink";

    public static final String PERSISTENT_PUBLIC_DEFAULT_PAGE_VIEW_SINK = "persistent://public/default/mail_stat_page_view_sink";
    public static final String PERSISTENT_PUBLIC_DEFAULT_APP_UV_SINK = "persistent://public/default/mail_stat_app_uv_sink";

    public static final String PERSISTENT_PUBLIC_DEFAULT_PAGE_STAY_SINK = "persistent://public/default/mail_stat_page_stay_sink";

    public static String getReferer(String referer, Context context) {
        if (StrUtil.isNotEmpty(referer)) {
            try {
                URL url = new URL(referer);
                return url.getHost();
            } catch (MalformedURLException e) {
                context.getLogger().error("提取referer失败", e);
            }
        }
        return "";
    }

    @Override
    public Void process(LogRecord input, Context context) throws Exception {
        try {
            JSONObject data = (JSONObject) JSON.toJSON(input.getData());
            // 生成设备id
            String deviceId = StrUtil.isBlank(input.getUuid()) ? generateDeviceId(input, data) : input.getUuid();
            // 判断是否新请求
            boolean isNew = context.getState(deviceId) == null;
            // 增加统计次数
            context.incrCounter(deviceId, 1L);

            // 提取referer
            String referer = getReferer(input.getReferer(), context);

            // 解析信息
            UserAgent agent = UserAgentUtil.parse(input.getUserAgent());
            String browser = agent.getBrowser().getName();
            String browserVersion = agent.getVersion();
            String platform = agent.getPlatform().getName();
            String os = agent.getOs().getName();
            String osVersion = agent.getOsVersion();
            String engine = agent.getEngine().getName();
            String engineVersion = agent.getEngineVersion();
            boolean mobile = agent.isMobile();

            switch (input.getEvent()) {
                case "goods_click":
                    GoodClickRecord goodClickRecord = GoodClickRecord.builder()
                            .event(input.getEvent())
                            .uuid(deviceId)
                            .ip(input.getIp())
                            .userAgent(input.getUserAgent())
                            .referer(referer)
                            .ts(input.getTs())
                            .bizId(input.getBizId())
                            .totalCount(context.getCounter(deviceId))
                            .browser(browser)
                            .browserVersion(browserVersion)
                            .platform(platform)
                            .os(os)
                            .osVersion(osVersion)
                            .engine(engine)
                            .engineVersion(engineVersion)
                            .memberNo(data.getString("memberNo"))
                            .memberType(data.getString("memberType"))
                            .mmCode(data.getString("mmCode"))
                            .storeCode(data.getString("storeCode"))
                            .channel(data.getString("channel"))
                            .publishType(data.getString("publishType"))
                            .goodsCode(data.getString("goodsCode"))
                            .pageNo(data.getString("pageNo"))
                            .isNew(isNew ? 1 : 0)
                            .mobile(mobile ? 1 : 0)
                            .goodsType(data.getString("goodsType"))
                            .pageType(data.getString("pageType"))
                            .build();
                    // 商品点击
                    context.newOutputMessage((String) context.getUserConfigValueOrDefault("publish-goods-click-sink-topic",
                                    PERSISTENT_PUBLIC_DEFAULT_GOODS_CLICK_SINK), JSONSchema.of(GoodClickRecord.class))
                            .key(UUID.randomUUID().toString())
                            .property("language", "java")
                            .value(goodClickRecord)
                            .send();
                    break;
                case "page_view":
                    PageViewRecord pageViewRecord = PageViewRecord.builder()
                            .event(input.getEvent())
                            .uuid(deviceId)
                            .ip(input.getIp())
                            .userAgent(input.getUserAgent())
                            .referer(referer)
                            .ts(input.getTs())
                            .bizId(input.getBizId())
                            .totalCount(context.getCounter(deviceId))
                            .browser(browser)
                            .browserVersion(browserVersion)
                            .platform(platform)
                            .os(os)
                            .osVersion(osVersion)
                            .engine(engine)
                            .engineVersion(engineVersion)
                            .memberNo(data.getString("memberNo"))
                            .memberType(data.getString("memberType"))
                            .mmCode(data.getString("mmCode"))
                            .storeCode(data.getString("storeCode"))
                            .channel(data.getString("channel"))
                            .publishType(data.getString("publishType"))
                            .pageNo(data.getString("pageNo"))
                            .pageUrl(data.getString("pageUrl"))
                            .isNew(isNew ? 1 : 0)
                            .mobile(mobile ? 1 : 0)
                            .pageType(data.getString("pageType"))
                            .build();
                    // 商品点击
                    context.newOutputMessage((String) context.getUserConfigValueOrDefault("publish-page-view-topic",
                                    PERSISTENT_PUBLIC_DEFAULT_PAGE_VIEW_SINK), JSONSchema.of(PageViewRecord.class))
                            .key(UUID.randomUUID().toString())
                            .property("language", "java")
                            .value(pageViewRecord)
                            .send();
                    // 页面浏览
                    break;
                case "page_stay":
                    PageStayRecord pageStayRecord = PageStayRecord.builder()
                            .event(input.getEvent())
                            .uuid(deviceId)
                            .ip(input.getIp())
                            .userAgent(input.getUserAgent())
                            .referer(referer)
                            .ts(input.getTs())
                            .bizId(input.getBizId())
                            .totalCount(context.getCounter(deviceId))
                            .browser(browser)
                            .browserVersion(browserVersion)
                            .platform(platform)
                            .os(os)
                            .osVersion(osVersion)
                            .engine(engine)
                            .engineVersion(engineVersion)
                            .memberNo(data.getString("memberNo"))
                            .memberType(data.getString("memberType"))
                            .mmCode(data.getString("mmCode"))
                            .storeCode(data.getString("storeCode"))
                            .channel(data.getString("channel"))
                            .publishType(data.getString("publishType"))
                            .pageNo(data.getString("pageNo"))
                            .stayTime(data.getLong("stayTime"))
                            .pageNo(data.getString("pageNo"))
                            .isNew(isNew ? 1 : 0)
                            .mobile(mobile ? 1 : 0)
                            .pageType(data.getString("pageType"))
                            .goodsType(data.getString("goodsType"))
                            .build();
                    context.newOutputMessage((String) context.getUserConfigValueOrDefault("publish-page-stay-topic",
                                    PERSISTENT_PUBLIC_DEFAULT_PAGE_STAY_SINK), JSONSchema.of(PageStayRecord.class))
                            .key(UUID.randomUUID().toString())
                            .property("language", "java")
                            .value(pageStayRecord)
                            .send();
                    break;
                // app UV采集
                case "app_uv":
                    AppUvRecord appUvRecord = AppUvRecord.builder()
                            .event(input.getEvent())
                            .uuid(deviceId)
                            .ip(input.getIp())
                            .userAgent(input.getUserAgent())
                            .referer(referer)
                            .ts(input.getTs())
                            .bizId(input.getBizId())
                            .totalCount(context.getCounter(deviceId))
                            .browser(browser)
                            .browserVersion(browserVersion)
                            .platform(platform)
                            .os(os)
                            .osVersion(osVersion)
                            .engine(engine)
                            .engineVersion(engineVersion)
                            .memberNo(data.getString("memberNo"))
                            .isNew(isNew ? 1 : 0)
                            .mobile(mobile ? 1 : 0)
                            .build();
                    context.newOutputMessage((String) context.getUserConfigValueOrDefault("publish-app-uv-topic",
                                    PERSISTENT_PUBLIC_DEFAULT_APP_UV_SINK), JSONSchema.of(AppUvRecord.class))
                            .key(UUID.randomUUID().toString())
                            .property("language", "java")
                            .value(appUvRecord)
                            .send();
                    break;
                default:
                    break;
            }
        } catch (PulsarClientException e) {
            context.getLogger().error("Function报错", e);
        }
        return null;
    }


    private String generateDeviceId(LogRecord input, JSONObject data) {
        Map<String, String> map = new TreeMap<>();
        map.put("ip", input.getIp());
        map.put("event", input.getEvent());
        map.put("userAgent", input.getUserAgent());
        if (StringUtils.isNotEmpty(data.getString("memberNo"))) {
            map.put("memberNo", input.getUserAgent());
        }
        return DeviceUtil.generateWebUniqueDeviceId(map);
    }

}
