package com.makro.mall.common.util;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description 多语言工具
 * @date 2021/11/11
 */
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MessageUtils {

    private final MessageSource messageSource;

    public String get(String key) {
        String text = messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
        return StrUtil.isNotEmpty(text) ? text : key;
    }

    public String get(String key, Object... params) {
        String text = messageSource.getMessage(key, params, key, LocaleContextHolder.getLocale());
        return StrUtil.isNotEmpty(text) ? text : key;
    }

}
