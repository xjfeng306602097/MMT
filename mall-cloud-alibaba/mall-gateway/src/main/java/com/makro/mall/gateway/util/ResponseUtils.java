package com.makro.mall.gateway.util;

import com.alibaba.fastjson2.JSON;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Author xiaojunfeng
 * @Date 2021-10-14 13:30
 */
public class ResponseUtils {

    public static Mono<Void> writeErrorInfo(ServerHttpResponse response, StatusCode statusCode) {
        if (StatusCode.ACCESS_UNAUTHORIZED.equals(statusCode) || StatusCode.TOKEN_INVALID_OR_EXPIRED.equals(statusCode)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
        } else if (StatusCode.TOKEN_ACCESS_FORBIDDEN.equals(statusCode)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
        } else {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        }
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set("Access-Control-Allow-Origin", "*");
        response.getHeaders().set("Cache-Control", "no-cache");
        String body = JSON.toJSONString(BaseResponse.error(statusCode));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
    }

}
