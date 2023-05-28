package com.makro.mall.mongo.util;

import org.springframework.data.mongodb.core.aggregation.GroupOperation;

import java.lang.reflect.Field;

/**
 * @author xiaojunfeng
 * @since 2021/4/15
 */
public abstract class MongoUtil {

    /**
     * 根据类自动生成 group first，主要是用来将查询的数据映射成目标类实体
     * <p>
     * See https://docs.mongodb.com/manual/reference/operator/aggregation/first/
     *
     * @param groupOperation 第一个group操作
     * @param clazz          被最终映射的类
     * @return group first 操作
     */
    public static GroupOperation groupFirst(GroupOperation groupOperation, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            String name = field.getName();
            if ("id".equals(name)) {
                continue;
            }
            groupOperation = groupOperation.first("$" + name).as(name);
        }
        return groupOperation;
    }

}
