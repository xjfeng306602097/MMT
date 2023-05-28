package com.makro.mall.stat.util;

import com.alibaba.fastjson2.util.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author admin
 */
@Component
public abstract class ClickHouseDateUtil {
    /**
     * 功能描述:生成sql BETWEEN toDate(#{startTime}) AND toDate(#{endTime})
     *
     * @Param:
     * @Return:
     * @Author: 卢嘉俊
     * @Date: 2022/7/6 用户行为分析
     */
    public static String format2Str(Date startTime, Date endTime) {
        return String.format("BETWEEN toDate('%s') AND toDate('%s')", DateUtils.format(startTime), DateUtils.format(endTime));
    }

    public static String formatEqualsStr(Date time) {
        return String.format("= toDate('%s')", DateUtils.format(time));
    }

    public static String format2DateTimeStr(Date startTime, Date endTime) {
        return String.format("BETWEEN toDateTime('%s') AND toDateTime('%s')", DateUtils.format(startTime), DateUtils.format(endTime));
    }

    public static String format2Str(Date time) {
        return String.format("toDate('%s')", DateUtils.format(time));
    }
}
