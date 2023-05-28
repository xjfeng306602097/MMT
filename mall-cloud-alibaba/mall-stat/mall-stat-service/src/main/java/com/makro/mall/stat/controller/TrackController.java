package com.makro.mall.stat.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.mq.producer.StatClickProducer;
import com.makro.mall.stat.pojo.dto.AppUvRequest;
import com.makro.mall.stat.pojo.dto.GoodsClickRequest;
import com.makro.mall.stat.pojo.dto.PageViewRequest;
import com.makro.mall.stat.pojo.entity.PageStayRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 跟踪接口
 * @date 2022/2/17
 */
@RestController
@RequestMapping("/api/v1/track")
@RequiredArgsConstructor
@Api(tags = "采集接口")
@RefreshScope
public class TrackController {

    @Value("${track.channel:open}")
    public String channel;

    private final StatClickProducer statClickProducer;

    @ApiOperation(value = "商品点击数据采集")
    @PostMapping("/goods/click")
    public BaseResponse goodsClick(@RequestBody @Validated GoodsClickRequest goodsClickRequest) {
        if (StrUtil.isBlank(goodsClickRequest.getMmCode())) {
            return BaseResponse.success();
        }
        if (ObjectUtil.equal(channel,"open") && StrUtil.isBlank(goodsClickRequest.getChannel())) {
            return BaseResponse.success();
        }
        statClickProducer.sendGoodsClickRequest(goodsClickRequest);
        return BaseResponse.success();
    }

    @ApiOperation(value = "页面展示数据采集")
    @PostMapping("/pv")
    public BaseResponse pageView(@RequestBody PageViewRequest pageViewRequest) {
        if (StrUtil.isBlank(pageViewRequest.getMmCode())) {
            return BaseResponse.success();
        }
        if (ObjectUtil.equal(channel,"open") && StrUtil.isBlank(pageViewRequest.getChannel())) {
            return BaseResponse.success();
        }
        statClickProducer.sendPageView(pageViewRequest);
        return BaseResponse.success(pageViewRequest.getBizId());
    }

    @ApiOperation(value = "APP UV采集")
    @PostMapping("/uv")
    public BaseResponse appUv(@RequestBody AppUvRequest appUvRequest) {
        String uuid = statClickProducer.sendAppUv(appUvRequest);
        return BaseResponse.success(uuid);
    }

    @ApiOperation(value = "页面停留采集")
    @PostMapping("/stay")
    public BaseResponse pageStay(@RequestBody List<PageStayRecord> pageStayRecords) {
        pageStayRecords.stream()
                .filter(x -> StrUtil.isBlank(x.getMmCode()))
                .filter(x -> ObjectUtil.equal(channel,"open") && StrUtil.isBlank(x.getChannel()))
                .forEach(pageStayRecords::remove);
        statClickProducer.delaySendPageStay(pageStayRecords);
        return BaseResponse.success();
    }

}
