package com.makro.mall.common.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/17
 */
public class MakroReflectionUtils extends ReflectionUtils {

    /**
     * Get {@link Field} Value
     *
     * @param object    {@link Object}
     * @param fieldName field name
     * @param <T>       field type
     * @return {@link Field} Value
     */
    public static <T> T getFieldValue(Object object, String fieldName) {
        return (T) getFieldValue(object, fieldName, null);
    }

    /**
     * Get {@link Field} Value
     *
     * @param object       {@link Object}
     * @param fieldName    field name
     * @param <T>          field type
     * @param defaultValue default value
     * @return {@link Field} Value
     */
    public static <T> T getFieldValue(Object object, String fieldName, T defaultValue) {
        T value = getFieldValue(object, fieldName);
        return value != null ? value : defaultValue;
    }

    /**
     * Get {@link Field} Value
     *
     * @param object    {@link Object}
     * @param fieldName field name
     * @param fieldType field type
     * @param <T>       field type
     * @return {@link Field} Value
     */
    public static <T> T getFieldValue(Object object, String fieldName, Class<T> fieldType) {
        T fieldValue = null;
        Field field = findField(object.getClass(), fieldName, fieldType);
        if (field != null) {
            boolean accessible = field.canAccess(object);
            try {
                if (!accessible) {
                    field.setAccessible(true);
                }
                fieldValue = (T) getField(field, object);
            } finally {
                if (!accessible) {
                    field.setAccessible(true);
                }
            }
        }
        return fieldValue;

    }

}
