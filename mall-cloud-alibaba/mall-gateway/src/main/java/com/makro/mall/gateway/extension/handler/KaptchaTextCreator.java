package com.makro.mall.gateway.extension.handler;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author xiaojunfeng
 * @description 验证码文本生成器
 * @date 2021/10/20
 */
public class KaptchaTextCreator extends DefaultTextCreator {

    private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");
    private SecureRandom rand = SecureRandom.getInstanceStrong();

    public KaptchaTextCreator() throws NoSuchAlgorithmException {
    }

    @Override
    public String getText() {
        Integer result = 0;
        int x = this.rand.nextInt(10);
        int y = this.rand.nextInt(10);
        StringBuilder str = new StringBuilder();
        int randomOperands = (int) Math.round(rand.nextDouble() * 2);
        switch (randomOperands) {
            case 0:
                result = x * y;
                str.append(NUMBERS[x]);
                str.append("*");
                str.append(NUMBERS[y]);
                break;
            case 1:
                if ((x != 0) && y % x == 0) {
                    result = y / x;
                    str.append(NUMBERS[y]);
                    str.append("/");
                    str.append(NUMBERS[x]);
                } else {
                    result = x + y;
                    str.append(NUMBERS[x]);
                    str.append("+");
                    str.append(NUMBERS[y]);
                }
                break;
            case 2:
                if (x >= y) {
                    result = x - y;
                    str.append(NUMBERS[x]);
                    str.append("-");
                    str.append(NUMBERS[y]);
                } else {
                    result = y - x;
                    str.append(NUMBERS[y]);
                    str.append("-");
                    str.append(NUMBERS[x]);
                }
                break;
            default:
                result = x + y;
                str.append(NUMBERS[x]);
                str.append("+");
                str.append(NUMBERS[y]);
                break;
        }
        str.append("=?@" + result);
        return str.toString();
    }

}
