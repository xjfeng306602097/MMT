package com.makro.mall.auth.security.extension;


import com.makro.mall.auth.constants.AuthClientConstants;
import com.makro.mall.common.enums.AuthenticationMethodEnum;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.AuthStatusCode;
import com.makro.mall.common.web.util.RequestUtils;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * 刷新token再次认证 UserDetailsService
 *
 * @author xianrui
 * @date 2021/10/2
 */
@NoArgsConstructor
public class PreAuthenticatedUserDetailsService<T extends Authentication> implements AuthenticationUserDetailsService<T>, InitializingBean {

    private Map<String, UserDetailsService> userDetailsServiceMap;

    private static final List<String> CONSTANT_LIST = List.of(AuthClientConstants.ADMIN_CLIENT_ID, AuthClientConstants.MALL_API);

    public PreAuthenticatedUserDetailsService(Map<String, UserDetailsService> userDetailsServiceMap) {
        Assert.notNull(userDetailsServiceMap, AuthStatusCode.USERDETAILSSERVICE_CANNOT_BE_NULL);
        this.userDetailsServiceMap = userDetailsServiceMap;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.userDetailsServiceMap, AuthStatusCode.USERDETAILSSERVICE_MUST_BE_SET);
    }

    @Override
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        String clientId = RequestUtils.getOAuth2ClientId();
        AuthenticationMethodEnum authenticationMethodEnum = AuthenticationMethodEnum.getByValue(RequestUtils.getAuthenticationMethod());
        UserDetailsService userDetailsService = userDetailsServiceMap.get(clientId);
        if (CONSTANT_LIST.contains(clientId)) {
            switch (authenticationMethodEnum) {
                default:
                    return userDetailsService.loadUserByUsername(authentication.getName());
            }
        } else {
            return userDetailsService.loadUserByUsername(authentication.getName());
        }
    }
}
