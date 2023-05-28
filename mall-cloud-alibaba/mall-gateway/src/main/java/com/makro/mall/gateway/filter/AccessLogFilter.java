package com.makro.mall.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson2.JSON;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.gateway.mq.producer.SysUserLogProducer;
import com.makro.mall.gateway.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Component
@Order(-100)
public class AccessLogFilter implements GlobalFilter {

    @Autowired
    private SysUserLogProducer sysUserLogProducer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            //封装数据
            ServerHttpRequest request = exchange.getRequest();

            //过滤没有用户token的
            List<String> authorization = request.getHeaders().get("Authorization");
            if (CollUtil.isEmpty(authorization)) {
                return chain.filter(exchange);
            }
            String payload = authorization.get(0);
            if (StrUtil.isBlank(payload) || StrUtil.equals("Basic bWFsbC1hZG1pbi13ZWI6MTIzNDU2", payload)) {
                return chain.filter(exchange);
            }
            //过滤请求/api/v1/sys/log/user
            String url = request.getURI().toString();
            int startIndex = url.indexOf('/', url.indexOf('/') + 2);
            String path = url.substring(startIndex);
            if (StrUtil.equals("/api/v1/sys/log/user", path)) {
                return chain.filter(exchange);
            }

            JWT jwt = JWTUtil.parseToken(StrUtil.removeAll(payload, "Bearer "));

            SystemUserLog gatewayLog = SystemUserLog.builder()
                    .type("api")
                    .userId(jwt.getPayload(AuthConstants.USER_ID_KEY).toString())
                    .userName(jwt.getPayload(AuthConstants.USER_NAME_KEY).toString())
                    .content(request.getMethod() + ":" + path)
                    .createTime(LocalDateTime.now())
                    .userIp(IpUtil.getIpAddr(request))
                    .build();
            sysUserLogProducer.save(JSON.toJSONBytes(gatewayLog));

        } catch (Exception e) {
            log.error("AccessLogFilter 报错", e);
        }
        return chain.filter(exchange);
    }


}
