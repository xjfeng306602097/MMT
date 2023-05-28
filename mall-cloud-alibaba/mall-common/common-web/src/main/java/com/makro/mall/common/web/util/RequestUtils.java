package com.makro.mall.common.web.util;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.common.enums.AuthenticationMethodEnum;
import com.nimbusds.jose.JWSObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 请求工具类
 *
 * @author xianrui
 */
@Slf4j
public class RequestUtils {
    @SneakyThrows
    public static String getGrantType() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String grantType = request.getParameter(AuthConstants.GRANT_TYPE_KEY);
        return grantType;
    }


    /**
     * 获取登录认证的客户端ID
     * <p>
     * 兼容两种方式获取OAuth2客户端信息（client_id、client_secret）
     * 方式一：client_id、client_secret放在请求路径中
     * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
     */
    @SneakyThrows
    public static String getOAuth2ClientId() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 从请求路径中获取
        String clientId = request.getParameter(AuthConstants.CLIENT_ID_KEY);
        if (StringUtils.hasLength(clientId)) {
            return clientId;
        }
        // 从请求头获取
        String basic = request.getHeader(AuthConstants.AUTHORIZATION_KEY);
        if (StringUtils.hasLength(basic) && basic.startsWith(AuthConstants.BASIC_PREFIX)) {
            basic = basic.replace(AuthConstants.BASIC_PREFIX, Strings.EMPTY);
            String basicPlainText = new String(Base64.getDecoder().decode(basic.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            //client:secret
            clientId = basicPlainText.split(":")[0];
        }
        return clientId;
    }

    /**
     * 解析JWT获取获取认证方式
     */
    @SneakyThrows
    public static String getAuthenticationMethod() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String refreshToken = request.getParameter(AuthConstants.REFRESH_TOKEN);

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(JWSObject.parse(refreshToken).getPayload().toJSONObject()));

        String authenticationMethod = jsonObject.getString(AuthConstants.AUTHENTICATION_METHOD);
        if (StringUtils.hasLength(authenticationMethod)) {
            authenticationMethod = AuthenticationMethodEnum.USERNAME.getValue();
        }
        return authenticationMethod;
    }
}
