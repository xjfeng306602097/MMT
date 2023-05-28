package com.makro.mall.gateway.extension.handler;

import com.google.common.cache.*;
import com.makro.mall.gateway.extension.file.component.MinioFileComponent;
import com.makro.mall.gateway.extension.file.config.MinioProperties;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description 验证码图形生成
 * @date 2021/10/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class H5ViewHandler implements HandlerFunction<ServerResponse> {

    private final MinioFileComponent minioFileComponent;

    private final MinioProperties minioProperties;

    private final Map<String, DataBuffer> dataBufferMap = new ConcurrentHashMap<>();

    private final LoadingCache<String, DataBuffer> CACHE = CacheBuilder.newBuilder()
            .maximumSize(50)
            .concurrencyLevel(8)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .removalListener(new RemovalListener<String, DataBuffer>() {
                @Override
                public void onRemoval(RemovalNotification<String, DataBuffer> notification) {
                    log.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                    DataBufferUtils.release(notification.getValue());
                }
            })
            .build(new CacheLoader<String, DataBuffer>() {
                @Override
                public DataBuffer load(@NotNull String filename) throws Exception {
                    String suffix = filename.substring(filename.lastIndexOf(".") + 1);
                    String bucketName = minioProperties.getBucketName(suffix);
                    InputStream response = minioFileComponent.getObject(bucketName, filename);
                    NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                    return nettyDataBufferFactory.wrap(response.readAllBytes());
                }
            });

    /**
     * 调用KaptchaTextCreator生成文本，并通过com.google.code.kaptcha将文本生成图片
     *
     * @param serverRequest
     * @return
     */
    @org.jetbrains.annotations.NotNull
    @SneakyThrows
    @NotNull
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        ServerHttpResponse res = serverRequest.exchange().getResponse();
        String filename = serverRequest.exchange().getRequest().getPath().subPath(3).toString();
        assert filename != null;
        DataBuffer dataBuffer = CACHE.get(filename);
        DataBuffer finalDataBuffer = dataBuffer.retainedSlice(0, dataBuffer.writePosition());
        return ServerResponse.ok().
                contentType(MediaType.TEXT_HTML).
                body(((outputMessage, context) -> {
                    var resp = (ZeroCopyHttpOutputMessage) outputMessage;
                    return resp.writeWith(Flux.create(sink -> {
                        sink.next(finalDataBuffer);
                        sink.complete();
                    }));
                }));
    }

}
