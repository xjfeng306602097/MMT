package com.makro.mall.stat.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/27
 */
@Slf4j
public class DeviceUtil {

    public static String generateWebUniqueDeviceId(Map<String, String> params) {
        return md5(params.toString());
    }

    private static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : bytes) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception ignored) {
        }
        return null;
    }

}
