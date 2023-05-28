package com.makro.mall.common.mybatis.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.common.model.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT工具类
 *
 * @author xianrui
 */
@Slf4j
public class JwtUtils {

    @SneakyThrows
    public static JSONObject getJwtPayload() {
        String payload = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader(AuthConstants.JWT_PAYLOAD_KEY);
        if (null == payload) {
            throw new BusinessException("Please pass in the authentication header");
        }
        return JSON.parseObject(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()));
    }

    /**
     * 解析JWT获取用户ID
     *
     * @return
     */
    public static String getUserId() {
        return getJwtPayload().getString(AuthConstants.USER_ID_KEY);
    }

    /**
     * 解析JWT获取获取用户名
     *
     * @return 返回用户名
     */
    public static String getUsername() {
        return getJwtPayload().getString(AuthConstants.USER_NAME_KEY);
    }


    /**
     * JWT获取用户角色列表
     *
     * @return 角色列表
     */
    public static List<String> getRoles() {
        List<String> roles = null;
        JSONObject payload = getJwtPayload();
        if (payload.containsKey(AuthConstants.JWT_AUTHORITIES_KEY)) {
            roles = payload.getJSONArray(AuthConstants.JWT_AUTHORITIES_KEY).stream().map(Object::toString).collect(Collectors.toList());
        }
        return roles;
    }
}
