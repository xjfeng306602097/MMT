package com.makro.mall.common.web.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author xiaojunfeng
 * @description 国际化实现
 * @date 2021/11/12
 */
@Component
public class MakroLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        String language = request.getHeader("Makro-Accept-Language");
        if (defaultLocale != null && StrUtil.isEmpty(language)) {
            return defaultLocale;
        }
        if (StrUtil.isNotEmpty(language)) {
            String[] split = language.split("_");
            return new Locale(split[0], split[1]);
        }
        return null;
    }

    private Locale getDefaultLocale() {
        return Locale.CHINA;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    }
}
