package com.makro.mall.auth.security.extension.captcha;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.makro.mall.common.constants.AuthConstants;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户名+密码+验证码 授权模式
 *
 * @author xianrui
 * @date 2021/9/25
 */
public class CaptchaTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "captcha";
    private final AuthenticationManager authenticationManager;
    private StringRedisTemplate redisTemplate;

    public CaptchaTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager,
                               StringRedisTemplate redisTemplate
    ) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());

        String validateCode = parameters.get("validateCode");
        String uuid = parameters.get("uuid");

        Assert.isTrue(StringUtils.isNotBlank(validateCode), StatusCode.CAPTCHA_NOT_EMPTY);
        String validateCodeKey = AuthConstants.VALIDATE_CODE_PREFIX + uuid;
        String correctValidateCode = redisTemplate.opsForValue().get(validateCodeKey);
        Assert.isTrue(StrUtil.isNotBlank(correctValidateCode), StatusCode.CAPTCHA_TIME_OUT);
        if (!validateCode.equals(correctValidateCode)) {
            throw new BusinessException(StatusCode.CAPTCHA_ERROR);
        } else {
            redisTemplate.delete(validateCodeKey);
        }

        String username = parameters.get("username");
        String password = parameters.get("password");

        parameters.remove("password");
        parameters.remove("validateCode");
        parameters.remove("uuid");

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);

        try {
            userAuth = this.authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException var8) {
            throw new InvalidGrantException(var8.getMessage());
        } catch (BadCredentialsException var9) {
            throw new InvalidGrantException(var9.getMessage());
        }

        if (userAuth != null && userAuth.isAuthenticated()) {
            OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth);
        } else {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }
    }
}
