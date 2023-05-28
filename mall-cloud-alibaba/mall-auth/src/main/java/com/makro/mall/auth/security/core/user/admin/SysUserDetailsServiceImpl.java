package com.makro.mall.auth.security.core.user.admin;

import com.makro.mall.admin.api.UserFeignClient;
import com.makro.mall.admin.pojo.dto.UserAuthDTO;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 系统用户体系业务类
 *
 * @author xiaojunfeng
 */
@Service("sysUserDetailsService")
@Slf4j
@RequiredArgsConstructor
public class SysUserDetailsServiceImpl implements UserDetailsService {

    private final UserFeignClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserDetails userDetails = null;
        BaseResponse<UserAuthDTO> result = userFeignClient.getUserByUsername(username);
        if (result.isSuccess()) {
            UserAuthDTO user = result.getData();
            if (null != user) {
                userDetails = new SysUserDetails(user);
            }
        }
        if (userDetails == null) {
            throw new UsernameNotFoundException(StatusCode.USER_NOT_EXIST.getMsg());
        } else if (!userDetails.isEnabled()) {
            throw new DisabledException("The account has been disabled!");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("The account has been locked!");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("This account has expired!");
        }
        return userDetails;
    }

}
