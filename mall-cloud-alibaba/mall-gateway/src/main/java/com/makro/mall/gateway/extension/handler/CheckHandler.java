package com.makro.mall.gateway.extension.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.nimbusds.jose.JWSObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author jincheng
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CheckHandler implements HandlerFunction<ServerResponse> {

   private final RedisTemplate<String,String> redisTemplate;
    private static final LocalDateTime LDT = LocalDateTime.now();

    @SneakyThrows
    @NotNull
    @Override
    public Mono<ServerResponse> handle(@NotNull ServerRequest request) {
        log.info("check:{}", LDT);
        // 不是正确的的JWT不做解析处理
        String token = request.headers().asHttpHeaders().getFirst(AuthConstants.AUTHORIZATION_KEY);
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, AuthConstants.JWT_PREFIX)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(BodyInserters.fromValue(BaseResponse.error(StatusCode.TOKEN_INVALID_OR_EXPIRED)));
        }

        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在则拦截访问
        token = StrUtil.replaceIgnoreCase(token, AuthConstants.JWT_PREFIX, Strings.EMPTY);
        String payload = StrUtil.toString(JWSObject.parse(token).getPayload());
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String jti = jsonObject.getStr(AuthConstants.JWT_JTI);
        Boolean isBlack = redisTemplate.hasKey(AuthConstants.TOKEN_BLACKLIST_PREFIX + jti);
        if (Boolean.TRUE.equals(isBlack)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(BodyInserters.fromValue(BaseResponse.error(StatusCode.TOKEN_ACCESS_FORBIDDEN)));
        }

        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(BaseResponse.success()));
    }
}
