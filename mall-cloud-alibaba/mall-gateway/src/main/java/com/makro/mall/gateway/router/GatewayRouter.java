package com.makro.mall.gateway.router;

import com.makro.mall.gateway.extension.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author xiaojunfeng
 * @description 图形验证码路由配置
 * @date 2021/10/20
 */
@Configuration
public class GatewayRouter {

    @Bean
    public RouterFunction<ServerResponse> routeFunction(CaptchaImageHandler captchaImageHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/validate-code")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), captchaImageHandler);
    }

    @Bean
    public RouterFunction<ServerResponse> routeHealthCheck(HealthCheckHandler healthCheckHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/health-check")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), healthCheckHandler);
    }

    @Bean
    public RouterFunction<ServerResponse> routeHandleView(H5ViewHandler h5ViewHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/h5/**")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), h5ViewHandler);
    }

    @Bean
    public RouterFunction<ServerResponse> routeShortLink(ShortLinkRedirectHandler shortLinkRedirectHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/=**")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), shortLinkRedirectHandler);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMarkoProduct(MakroProductRedirectHandler makroProductRedirectHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/goto/product/**")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), makroProductRedirectHandler);
    }

    @Bean
    public RouterFunction<ServerResponse> routeIpAddress(IpAddressHandler ipAddressHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/ipAddress")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), ipAddressHandler);
    }

    @Bean
    public RouterFunction<ServerResponse> check(CheckHandler checkHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/check")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), checkHandler);
    }


}
