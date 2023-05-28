package com.makro.mall.auth.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.admin.api.UserFeignClient;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.auth.constants.AuthClientConstants;
import com.makro.mall.auth.mq.producer.SysUserLogProducer;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.web.util.IpUtil;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.common.web.util.RequestUtils;
import com.makro.mall.stat.pojo.entity.SystemUserLog;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description Oauth授权接口
 * @date 2021/10/11
 */
@Api(tags = "认证中心")
@RefreshScope
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private TokenEndpoint tokenEndpoint;
    private RedisTemplate redisTemplate;
    private KeyPair keyPair;
    private UserFeignClient userFeignClient;
    private SysUserLogProducer sysUserLogProducer;

    @ApiOperation(value = "OAuth2认证", notes = "登录入口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", defaultValue = "password", value = "授权模式", required = true),
            @ApiImplicitParam(name = "refresh_token", value = "刷新token"),
            @ApiImplicitParam(name = "username", defaultValue = "admin", value = "用户名"),
            @ApiImplicitParam(name = "password", defaultValue = "123456", value = "用户密码")
    })
    @PostMapping("/token")
    public BaseResponse<OAuth2AccessToken> postAccessToken(
            @ApiIgnore Principal principal,
            @ApiIgnore @RequestParam Map<String, String> parameters,
            HttpServletRequest request) throws HttpRequestMethodNotSupportedException, ParseException {
        /*
          获取登录认证的客户端ID

          兼容两种方式获取Oauth2客户端信息（client_id、client_secret）
          方式一：client_id、client_secret放在请求路径中(注：当前版本已废弃)
          方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
         */
        String clientId = RequestUtils.getOAuth2ClientId();
        log.info("OAuth认证授权 客户端ID:{}，请求参数：{}", clientId, JSON.toJSONString(parameters));
        /*
          knife4j接口文档测试使用

          请求头自动填充，token必须原生返回，否则显示 undefined undefined
          账号/密码:  client_id/client_secret : client/123456
         */
        if (AuthClientConstants.TEST_CLIENT_ID.equals(clientId)) {
            return BaseResponse.success(tokenEndpoint.postAccessToken(principal, parameters).getBody());
        }

        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();

        // 处理token缓存
        assert accessToken != null;
        cn.hutool.json.JSONObject object = processToken(accessToken);
        String userId = object.getStr(AuthConstants.USER_ID_KEY);


        // 处理登录信息
        String ipAddr = IpUtil.getIpAddr(request);
        SysUser user = new SysUser();
        user.setLastLoginIp(ipAddr);
        user.setLastLoginTime(new Date());
        userFeignClient.loginInfo(userId, user);

        SystemUserLog systemUserLog = SystemUserLog.builder()
                .type("login")
                .userId(userId)
                .userName(object.getStr(AuthConstants.USER_NAME_KEY))
                .userIp(ipAddr)
                .createTime(LocalDateTime.now())
                .build();
        sysUserLogProducer.save(systemUserLog);
        log.debug("SysUserLogProducer 发送消息:{}", JSON.toJSONString(systemUserLog));

        return BaseResponse.success(accessToken);
    }

    private cn.hutool.json.JSONObject processToken(OAuth2AccessToken accessToken) throws ParseException {
        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在则拦截访问
        String token = StrUtil.replaceIgnoreCase(accessToken.getValue(), AuthConstants.JWT_PREFIX, Strings.EMPTY);
        String payload = StrUtil.toString(JWSObject.parse(token).getPayload());
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(payload);
        String jti = jsonObject.getStr(AuthConstants.JWT_JTI);
        String userName = jsonObject.getStr(AuthConstants.USER_NAME_KEY);
        String key = String.format(RedisConstants.SYS_USER_TOKEN, userName);
        // 判断是否存在对应的token
        String lastJti = (String) redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotEmpty(lastJti)) {
            // 将之前的jti加入黑名单
            Long expire = redisTemplate.opsForValue().getOperations().getExpire(key);
            redisTemplate.opsForValue().set(AuthConstants.TOKEN_BLACKLIST_PREFIX + lastJti, null, expire, TimeUnit.SECONDS);
        }
        // 重新写入数据
        redisTemplate.opsForValue().set(key, jti, accessToken.getExpiresIn(), TimeUnit.SECONDS);
        return jsonObject;
    }

    @ApiOperation(value = "注销")
    @DeleteMapping("/logout")
    public BaseResponse logout() {
        JSONObject payload = JwtUtils.getJwtPayload();
        // JWT唯一标识
        String jti = payload.getString(AuthConstants.JWT_JTI);
        // JWT过期时间戳(单位：秒)
        Long expireTime = payload.getLong(AuthConstants.JWT_EXP);
        if (expireTime != null) {
            // 当前时间（单位：秒）
            long currentTime = System.currentTimeMillis() / 1000;
            // token未过期，添加至缓存作为黑名单限制访问，缓存时间为token过期剩余时间
            if (expireTime > currentTime) {
                redisTemplate.opsForValue().set(AuthConstants.TOKEN_BLACKLIST_PREFIX + jti, null, (expireTime - currentTime), TimeUnit.SECONDS);
            }
        } else {
            // token 永不过期则永久加入黑名单
            redisTemplate.opsForValue().set(AuthConstants.TOKEN_BLACKLIST_PREFIX + jti, null);
        }

        return BaseResponse.success("注销成功");
    }

    @ApiOperation(value = "获取公钥")
    @GetMapping("/public-key")
    public Map<String, Object> getPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }
}
