package com.makro.mall.gateway.filter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.gateway.util.ResponseUtils;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * @author jincheng
 */
@Slf4j
@Component
@Order(2)
@RefreshScope
public class VerifyTheSignatureFilter implements GlobalFilter {

    private static final String SIGNATURE_HEADER = "makro-digital-sign";
    private static final String SECRET_KEY = "makro_jE7n85j";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        ServerHttpRequest request = exchange.getRequest();
        //过滤请求/api/v1/track
        if (StrUtil.contains(request.getURI().toString(), "/api/v1/track")) {
            String sign = request.getHeaders().getFirst(SIGNATURE_HEADER);
            if (StrUtil.isNotEmpty(sign)) {
                Date date1 = new Date();
                String date = DateUtil.format(date1, DatePattern.PURE_DATE_FORMAT);
                Flux<DataBuffer> body = request.getBody();
                return DataBufferUtils.join(body)
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            String bodyString = new String(bytes, StandardCharsets.UTF_8);
                            String sign2 = SecureUtil.md5(SECRET_KEY + bodyString + date);
                            //正常验签逻辑
                            if (StrUtil.equals(sign, sign2)) {
                                ServerHttpRequest modifiedRequest = getServerHttpRequest(request, bytes);
                                return filterChain.filter(exchange.mutate().request(modifiedRequest).build());
                            }

                            //处理跨天验签逻辑
                            if (DateUtil.thisHour(true) == 0 && DateUtil.thisMinute() < 10) {
                                String sign3 = SecureUtil.md5(SECRET_KEY + bodyString + DateUtil.yesterday());
                                if (StrUtil.equals(sign, sign3)) {
                                    ServerHttpRequest modifiedRequest = getServerHttpRequest(request, bytes);
                                    return filterChain.filter(exchange.mutate().request(modifiedRequest).build());
                                }
                            }
                            // 如果签名验证失败，立即返回错误响应
                            return ResponseUtils.writeErrorInfo(exchange.getResponse(), StatusCode.CHECK_SIGNATURE_ERROR).then(Mono.empty());
                        });
            }
        }
        return filterChain.filter(exchange);
    }

    @NotNull
    private static ServerHttpRequest getServerHttpRequest(ServerHttpRequest request, byte[] bytes) {
        // 构建新的请求对象
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer modifiedBodyDataBuffer = nettyDataBufferFactory.wrap(bytes);
        Flux<DataBuffer> modifiedBody = Flux.just(modifiedBodyDataBuffer);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.setContentLength(bytes.length);
        ServerHttpRequest modifiedRequest = new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return modifiedBody;
            }
        };
        return modifiedRequest;
    }
}