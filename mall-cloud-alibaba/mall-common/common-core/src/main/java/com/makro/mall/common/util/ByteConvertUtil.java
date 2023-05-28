package com.makro.mall.common.util;

import cn.hutool.core.util.NumberUtil;

/**
 * 功能描述:转换Byte
 *
 * @Author: 卢嘉俊
 * @Date: 2022/7/18 font显示文件大小
 */
public abstract class ByteConvertUtil {


    /**
     * 功能描述: 字节转兆 如果小于0.01兆则全显示 否则保留两位小数
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/18 用户行为分析
     */
    public static double b2Mb(long bytes) {
        double div = NumberUtil.div(bytes, 1048576);
        if (NumberUtil.compare(div, 0.01) >= 0) {
            div = NumberUtil.round(div, 2).doubleValue();
        }
        return div;
    }
}
