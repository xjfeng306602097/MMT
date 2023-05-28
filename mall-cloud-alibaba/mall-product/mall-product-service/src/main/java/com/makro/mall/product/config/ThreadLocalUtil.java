package com.makro.mall.product.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/2
 **/
public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> holder = new ThreadLocal<Map<String, Object>>();

    public static void set(String key, Object obj) {
        getContext().put(key, obj);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                holder.remove();
            }
        }, 2 * 60 * 1000);
    }

    public static Object get(String key) {
        return getContext().get(key);
    }

    public static void remove() {
        holder.remove();
    }

    static private Map<String, Object> getContext() {
        Map map = holder.get();
        if (map == null) {
            map = new HashMap();
            holder.set(map);
        }
        return map;
    }
}
