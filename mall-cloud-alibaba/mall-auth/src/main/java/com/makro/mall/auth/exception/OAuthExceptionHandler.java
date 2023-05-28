package com.makro.mall.auth.exception;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class OAuthExceptionHandler {

    /**
     * 用户不存在
     *
     * @param e
     * @return
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public BaseResponse<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return BaseResponse.error(StatusCode.USER_NOT_EXIST);
    }

    /**
     * 用户名和密码异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(InvalidGrantException.class)
    public BaseResponse<String> handleInvalidGrantException(InvalidGrantException e) {
        return BaseResponse.error(StatusCode.USERNAME_OR_PASSWORD_ERROR);
    }


    /**
     * 账户异常(禁用、锁定、过期)
     *
     * @param e
     * @return
     */
    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public BaseResponse<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return BaseResponse.error(e.getMessage());
    }

    /**
     * token 无效或已过期
     *
     * @param e
     * @return
     */
    @ExceptionHandler({InvalidTokenException.class})
    public BaseResponse handleInvalidTokenExceptionException(InvalidTokenException e) {
        return BaseResponse.error(e.getMessage());
    }

}
