package com.makro.mall.gateway.extension.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.IdUtil;
import com.google.code.kaptcha.Producer;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description 验证码图形生成
 * @date 2021/10/20
 */
@Component
@RequiredArgsConstructor
public class CaptchaImageHandler implements HandlerFunction<ServerResponse> {

    private final Producer producer;
    private final StringRedisTemplate redisTemplate;

    /**
     * 调用KaptchaTextCreator生成文本，并通过com.google.code.kaptcha将文本生成图片
     *
     * @param serverRequest
     * @return
     */
    @NotNull
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        // 生成验证码
        String capText = producer.createText();
        // 通过@进行分割，左边是公式，用于生成图片，右边是答案，用于存放到redis，在makro-auth服务进行validate-code验证
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        String code = capText.substring(capText.lastIndexOf("@") + 1);
        BufferedImage image = producer.createImage(capStr);
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        // 存入redis
        redisTemplate.opsForValue().set(AuthConstants.VALIDATE_CODE_PREFIX + uuid, code, 60, TimeUnit.SECONDS);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return Mono.error(e);
        }
        Map<String, String> resultMap = new HashMap<String, String>(8);
        resultMap.put("uuid", uuid);
        resultMap.put("img", Base64.encode(os.toByteArray()));
        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(BaseResponse.success(resultMap)));
    }
}
