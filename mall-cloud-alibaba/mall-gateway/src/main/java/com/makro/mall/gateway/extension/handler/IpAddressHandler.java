package com.makro.mall.gateway.extension.handler;

import com.google.common.collect.Lists;
import com.makro.mall.common.health.service.HealthCheckService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.util.NetUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author xiaojunfeng
 * @description 验证码图形生成
 * @date 2021/10/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IpAddressHandler implements HandlerFunction<ServerResponse> {

    private final StringRedisTemplate redisTemplate;

    private final HealthCheckService healthCheckService;

    /**
     * 调用KaptchaTextCreator生成文本，并通过com.google.code.kaptcha将文本生成图片
     *
     * @param serverRequest
     * @return
     */
    @NotNull
    @SneakyThrows
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        String localAddress = NetUtils.getLocalHost("eth1");
        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(BaseResponse.success(Lists.newArrayList(localAddress))));
    }

}
