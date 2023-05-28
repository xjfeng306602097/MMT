package com.makro.mall.gateway.config;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/12
 */
@Component
@Slf4j
public class LanguageFilter implements GlobalFilter, Ordered {

    private final ApplicationContext applicationContext;

    public LanguageFilter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String languageValue = StrUtil.isNotEmpty(request.getHeaders().getFirst("Makro-Accept-Language")) ?
                request.getHeaders().getFirst("Makro-Accept-Language") : getDefaultLanguage();
        log.debug("languageValue is {}", languageValue);
        assert languageValue != null;
        String[] str = languageValue.split("_");
        LocaleContextHolder.setLocale(new Locale(str[0], str[1]));

        return chain.filter(exchange);
    }

    private String getDefaultLanguage() {
        return "en_US";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
