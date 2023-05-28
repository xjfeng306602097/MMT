package com.makro.mall.stat.component;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/9/8
 */
@Component
@Slf4j
public class StatDelayCache {

    private final Map<Class, CopyOnWriteArrayList> delayMap = new ConcurrentHashMap<>(1000);
    private final Map<Class, Function<List, Boolean>> functionMap = new HashMap<>();

    public void register(Function<List, Boolean> syncFunction, Class clazz) {
        functionMap.put(clazz, syncFunction);
    }

    public void process(List data) {
        if (CollectionUtil.isNotEmpty(data)) {
            delayMap.computeIfAbsent(data.get(0).getClass(), k -> new CopyOnWriteArrayList<>());
            delayMap.get(data.get(0).getClass()).addAll(data);
        }
    }

    /**
     * 定时同步，将缓存额数据整合后
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void summaryAndSync() {
        Iterator<Map.Entry<Class, CopyOnWriteArrayList>> iterator = delayMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Class, CopyOnWriteArrayList> entry = iterator.next();
            Function<List, Boolean> syncFunction = functionMap.get(entry.getKey());
            syncFunction.apply(entry.getValue());
            entry.getValue().clear();
        }
    }

}
