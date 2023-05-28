package com.makro.mall.common.util;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 描述: OrikaBeanUtil
 *
 * @author xiaojunfeng
 * @date 2019/11/25 9:42
 **/
@Slf4j
public class OrikaBeanUtils {

    public static Map<String, MapperFactory> beanMapperFactoryMap = new ConcurrentHashMap<>();

    /**
     * @param source   来源
     * @param target   目标
     * @param mapNulls 是否映射空
     * @return
     */
    private static Object beanCopy(Object source, Object target, boolean mapNulls) {
        Object result = target;
        if (null != source && null != target) {
            String beanKey = generateKey(source.getClass(), target.getClass());
            MapperFactory mapperFactory = beanMapperFactoryMap.get(beanKey);
            if (mapperFactory == null) {
                mapperFactory = getDefaultMapperFactory(source.getClass(), target.getClass(), true);
                beanMapperFactoryMap.put(beanKey, mapperFactory);
            }
            MapperFacade mapper = mapperFactory.getMapperFacade();
            result = mapper.map(source, target.getClass());
            if (!mapNulls) {
                result = combine(result, target);
            }
        }
        return result;
    }

    /**
     * 全量复制
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copy(Object source, T target) {
        return (T) beanCopy(source, target, true);
    }

    /**
     * 非空复制
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyNotNull(Object source, T target) {
        return (T) beanCopy(source, target, false);
    }

    public static <T> T copy(Object source, Class<T> targetClass) {
        if (null != source && null != targetClass) {
            Object target = null;

            try {
                target = targetClass.newInstance();
            } catch (InstantiationException var4) {
                log.error("bean copy InstantiationException", var4);
            } catch (IllegalAccessException var5) {
                log.error("bean copy IllegalAccessException", var5);
            }

            if (null == target) {
                return null;
            } else {
                target = beanCopy(source, target, true);
                return (T) target;
            }
        } else {
            return null;
        }
    }

    public static <T> T copyNotNull(Object source, Class<T> targetClass) {
        if (null != source && null != targetClass) {
            Object target = null;

            try {
                target = targetClass.newInstance();
            } catch (InstantiationException var4) {
                log.error("bean copy InstantiationException", var4);
            } catch (IllegalAccessException var5) {
                log.error("bean copy IllegalAccessException", var5);
            }

            if (null == target) {
                return null;
            } else {
                target = beanCopy(source, target, false);
                return (T) target;
            }
        } else {
            return null;
        }
    }

    public static <T> List<T> copyList(List<?> source, Class<T> cls) {
        return source != null && !source.isEmpty() ? (List) source.parallelStream().map((t) -> {
            try {
                return copy(t, cls.newInstance());
            } catch (Exception var3) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList()) : Collections.emptyList();
    }


    private static MapperFactory getDefaultMapperFactory(Class source, Class target, boolean mapNulls) {
        MapperFactory mapperFactory;
        mapperFactory = new DefaultMapperFactory.Builder().mapNulls(mapNulls).build();
        mapperFactory.classMap(source, target).byDefault().register();
        return mapperFactory;
    }

    private static String generateKey(Class<?> class1, Class<?> class2) {
        return class1.toString() + class2.toString();
    }

    private static Object combine(Object sourceBean, Object targetBean) {
        Class sourceBeanClass = sourceBean.getClass();
        Class targetBeanClass = targetBean.getClass();

        Field[] sourceFields = sourceBeanClass.getDeclaredFields();
        Field[] targetFields = sourceBeanClass.getDeclaredFields();
        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            Field targetField = targetFields[i];
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if (!(sourceField.get(sourceBean) == null)) {
                    targetField.set(targetBean, sourceField.get(sourceBean));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return targetBean;
    }

}
