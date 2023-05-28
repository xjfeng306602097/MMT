package com.makro.mall.common.util;

import cn.hutool.core.codec.Base62;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/9/8 mm发布时用户id加密解码
 */
@Slf4j
public abstract class AesBase62Util {


    private static final SymmetricCrypto AES = new SymmetricCrypto(SymmetricAlgorithm.AES, "JinChenFaCai6666".getBytes());

    /**
     * 功能描述:
     *
     * @Author: 卢嘉俊
     * @Date: 2022/9/8 用户id加密
     */
    public static String encode(Long id) {
        //加密
        byte[] encrypt = AES.encrypt(String.valueOf(id));
        return Base62.encode(encrypt);
    }


    /**
     * 功能描述:
     *
     * @Author: 卢嘉俊
     * @Date: 2022/9/8 用户id解密
     */
    public static String decode(String encode) {
        //解密
        if (StrUtil.isBlank(encode)) {
            return null;
        }
        try {
            byte[] decode = Base62.decode(encode);
            byte[] decrypt = AES.decrypt(decode);
            return new String(decrypt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("用户id解密 失败 encode:{} e:{}", encode, ExceptionUtil.stacktraceToString(e));
            return null;
        }
    }
}
