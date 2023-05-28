package com.makro.mall.gateway.extension.handler;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author xiaojunfeng
 * @description 验证码图形生成
 * @date 2021/10/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShortLinkRedirectHandler implements HandlerFunction<ServerResponse>, RedisConstants {

    private final RedisUtils redisUtils;

    @NotNull
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        ServerWebExchange exchange = request.exchange();
        ServerHttpRequest httpRequest = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = httpRequest.getPath().value();
        String code = path.substring(path.lastIndexOf('/') + 2);
        String longUrl = (String) redisUtils.get(RedisConstants.SHORT_LINK_CODE_PREFIX + code);
        if (StrUtil.isEmpty(longUrl)) {
            return ServerResponse.status(HttpStatus.NOT_FOUND).build();
        }
        String url = httpRequest.getURI().toString();
        String paramDelimiter = "?";
        int paraIndex = url.lastIndexOf(paramDelimiter);
        if (paraIndex >= 0 && !url.endsWith(paramDelimiter)) {
            if (longUrl.contains(paramDelimiter)) {
                longUrl = longUrl + "&" + url.substring(paraIndex + 1);
            } else {
                longUrl = longUrl + paramDelimiter + url.substring(paraIndex + 1);
            }
        }
        response.getHeaders().set(HttpHeaders.LOCATION, longUrl);
        // 303 状态码表示由于请求对应的资源存在着另一个URI，应使用GET方法定向获取请求的资源
        return ServerResponse.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, longUrl)
                .header(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
                .build();
    }

}
