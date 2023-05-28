package com.makro.mall.common.util;

import java.util.Random;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/10
 */
public class PasswordUtil {

    /**
     * 大小写字母
     */
    public final static String[] WORD = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    /**
     * 数字 1-9
     */
    public final static String[] NUM = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    /**
     * 特殊字符
     */
    public final static String[] SYMBOL = {
            "!", "@", "#", "$", "%", "^", "&", "*",
            "(", ")", "{", "}", "[", "]", ".", "?", "_"
    };

    public final static String[] TYPE = {
            "WORD", "NUM", "symbol"
    };

    /**
     * 随机生成 8-12 位包含 字母、数字 的密码
     *
     * @return
     */
    public static String randomPassWORD() {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random(System.currentTimeMillis());
        boolean flag = false;
        //输出几位密码长度  这里是有可能8 ，9 ，10 位
        int length = random.nextInt(3) + 8;
        for (int i = 0; i < length; i++) {
            if (flag) {
                stringBuffer.append(NUM[random.nextInt(NUM.length)]);
            } else {
                stringBuffer.append(WORD[random.nextInt(WORD.length)]);
            }
            flag = !flag;
        }
        return stringBuffer.toString();
    }

    /**
     * 随机生成 n 位包含 字母、数字、特殊字符 的密码
     *
     * @return
     */
    public static String randomPW(Integer count) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random(System.currentTimeMillis());
        String flag = TYPE[random.nextInt(TYPE.length)];
        // 输出长度 12 位
        int length = count;
        for (int i = 0; i < length; i++) {
            switch (flag) {
                case "WORD":
                    stringBuffer.append(WORD[random.nextInt(WORD.length)]);
                    break;
                case "NUM":
                    stringBuffer.append(NUM[random.nextInt(NUM.length)]);
                    break;
                case "symbol":
                    stringBuffer.append(SYMBOL[random.nextInt(SYMBOL.length)]);
                    break;
                default:
                    break;
            }
            flag = TYPE[random.nextInt(TYPE.length)];
        }
        return stringBuffer.toString();
    }

}
