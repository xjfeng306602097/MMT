package com.makro.mall.common.model;

import org.slf4j.event.Level;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 断言类
 * @date 2021/10/9
 */
public class Assert {

    public Assert() {
    }

    public static void failed(String code, String message) {
        throw new BusinessException(new StatusCode(code, message, Level.WARN));
    }

    public static void failed(String code, String message, Level level) {
        throw new BusinessException(new StatusCode(code, message, level));
    }

    public static void failed(StatusCode code) {
        throw new BusinessException(code);
    }

    public static void failed(String message) {
        throw new BusinessException(message);
    }

    public static void isTrue(boolean expression, String code, String message) {
        if (!expression) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void isTrue(boolean expression, String code, String message, Level level) {
        if (!expression) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void isTrue(boolean expression, StatusCode code) {
        if (!expression) {
            throw new BusinessException(code);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(message);
        }
    }

    public static void notTrue(boolean expression, String code, String message) {
        if (expression) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void notTrue(boolean expression, String code, String message, Level level) {
        if (expression) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void notTrue(boolean expression, StatusCode code) {
        if (expression) {
            throw new BusinessException(code);
        }
    }

    public static void notTrue(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(message);
        }
    }

    public static void notNull(Object object, String code, String message) {
        if (ObjectUtils.isEmpty(object)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void notNull(Object object, String code, String message, Level level) {
        if (ObjectUtils.isEmpty(object)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void notNull(Object object, StatusCode code) {
        if (ObjectUtils.isEmpty(object)) {
            throw new BusinessException(code);
        }
    }

    public static void notNull(Object object, String message) {
        if (ObjectUtils.isEmpty(object)) {
            throw new BusinessException(message);
        }
    }

    public static void isNull(Object object, String code, String message) {
        if (!ObjectUtils.isEmpty(object)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void isNull(Object object, String code, String message, Level level) {
        if (!ObjectUtils.isEmpty(object)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void isNull(Object object, StatusCode code) {
        if (!ObjectUtils.isEmpty(object)) {
            throw new BusinessException(code);
        }
    }

    public static void isNull(Object object, String message) {
        if (!ObjectUtils.isEmpty(object)) {
            throw new BusinessException(message);
        }
    }

    public static void containsWhitespace(String str, String code, String message) {
        if (!StringUtils.containsWhitespace(str)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void containsWhitespace(String str, String code, String message, Level level) {
        if (!StringUtils.containsWhitespace(str)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void containsWhitespace(String str, StatusCode code) {
        if (!StringUtils.containsWhitespace(str)) {
            throw new BusinessException(code);
        }
    }

    public static void containsWhitespace(String str, String message) {
        if (!StringUtils.containsWhitespace(str)) {
            throw new BusinessException(message);
        }
    }

    public static void isNumeric(String numeric, String code, String message) {
        if (!isNumeric(numeric)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void isNumeric(String numeric, String code, String message, Level level) {
        if (!isNumeric(numeric)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void isNumeric(String numeric, StatusCode code) {
        if (!isNumeric(numeric)) {
            throw new BusinessException(code);
        }
    }

    public static void isNumeric(String numeric, String message) {
        if (!isNumeric(numeric)) {
            throw new BusinessException(message);
        }
    }

    private static boolean isNumeric(final CharSequence cs) {
        if (StringUtils.isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static void startWith(String str, String prefix, String code, String message) {
        notNull(str, code, message);
        if (!str.startsWith(prefix)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void startWith(String str, String prefix, String code, String message, Level level) {
        notNull(str, code, message, level);
        if (!str.startsWith(prefix)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void startWith(String str, String prefix, StatusCode code) {
        notNull(str, (StatusCode) code);
        if (!str.startsWith(prefix)) {
            throw new BusinessException(code);
        }
    }

    public static void startWith(String str, String prefix, String message) {
        notNull(str, (String) message);
        if (!str.startsWith(prefix)) {
            throw new BusinessException(message);
        }
    }

    public static void hasLength(String str, int length, String code, String message) {
        notNull(str, code, message);
        if (str.length() < length) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void hasLength(String str, int length, String code, String message, Level level) {
        notNull(str, code, message, level);
        if (str.length() < length) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void hasLength(String str, int length, StatusCode code) {
        notNull(str, (StatusCode) code);
        if (str.length() < length) {
            throw new BusinessException(code);
        }
    }

    public static void hasLength(String str, int length, String message) {
        notNull(str, (String) message);
        if (str.length() < length) {
            throw new BusinessException(message);
        }
    }

    public static void shortThan(String str, int length, String code, String message) {
        notNull(str, code, message);
        if (str.length() >= length) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void shortThan(String str, int length, String code, String message, Level level) {
        notNull(str, code, message, level);
        if (str.length() >= length) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void shortThan(String str, int length, StatusCode code) {
        notNull(str, (StatusCode) code);
        if (str.length() >= length) {
            throw new BusinessException(code);
        }
    }

    public static void shortThan(String str, int length, String message) {
        notNull(str, (String) message);
        if (str.length() >= length) {
            throw new BusinessException(message);
        }
    }

    public static void isPositive(Long val, String code, String message) {
        if (val == null || val <= 0L) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void isPositive(Long val, String code, String message, Level level) {
        if (val == null || val <= 0L) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void isPositive(Long val, StatusCode code) {
        if (val == null || val <= 0L) {
            throw new BusinessException(code);
        }
    }

    public static void isPositive(Long val, String message) {
        if (val == null || val <= 0L) {
            throw new BusinessException(message);
        }
    }

    public static void isEqual(Object expected, Object actual, String code, String message) {
        if (!ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void isEqual(Object expected, Object actual, String code, String message, Level level) {
        if (!ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void isEqual(Object expected, Object actual, StatusCode code) {
        if (!ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(code);
        }
    }

    public static void isEqual(Object expected, Object actual, String message) {
        if (!ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(message);
        }
    }

    public static void notEqual(Object expected, Object actual, String code, String message) {
        if (ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static void notEqual(Object expected, Object actual, String code, String message, Level level) {
        if (ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static void notEqual(Object expected, Object actual, StatusCode code) {
        if (ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(code);
        }
    }

    public static void notEqual(Object expected, Object actual, String message) {
        if (ObjectUtils.nullSafeEquals(expected, actual)) {
            throw new BusinessException(message);
        }
    }

    public static <T> void contains(List<T> l, T obj, String code, String message) {
        notNull(l, code, message);
        if (!ObjectUtils.containsElement(l.toArray(), obj)) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static <T> void contains(List<T> l, T obj, String code, String message, Level level) {
        notNull(l, code, message, level);
        if (!ObjectUtils.containsElement(l.toArray(), obj)) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static <T> void contains(List<T> l, T obj, StatusCode code) {
        notNull(l, (StatusCode) code);
        if (!ObjectUtils.containsElement(l.toArray(), obj)) {
            throw new BusinessException(code);
        }
    }

    public static <T> void contains(List<T> l, T obj, String message) {
        notNull(l, (String) message);
        if (!ObjectUtils.containsElement(l.toArray(), obj)) {
            throw new BusinessException(message);
        }
    }

    public static <T extends Enum<T>> void isValidEnum(Class<T> enumClass, String name, String code, String message) {
        if (Arrays.stream(enumClass.getEnumConstants()).noneMatch((g) -> {
            return g.name().equals(name);
        })) {
            throw new BusinessException(new StatusCode(code, message, Level.WARN));
        }
    }

    public static <T extends Enum<T>> void isValidEnum(Class<T> enumClass, String name, String code, String message, Level level) {
        if (Arrays.stream(enumClass.getEnumConstants()).noneMatch((g) -> {
            return g.name().equals(name);
        })) {
            throw new BusinessException(new StatusCode(code, message, level));
        }
    }

    public static <T extends Enum<T>> void isValidEnum(Class<T> enumClass, String name, StatusCode code) {
        if (Arrays.stream(enumClass.getEnumConstants()).noneMatch((g) -> {
            return g.name().equals(name);
        })) {
            throw new BusinessException(code);
        }
    }

    public static <T extends Enum<T>> void isValidEnum(Class<T> enumClass, String name, String message) {
        if (Arrays.stream(enumClass.getEnumConstants()).noneMatch((g) -> {
            return g.name().equals(name);
        })) {
            throw new BusinessException(message);
        }
    }

    public static void success(BaseResponse<?> resp) {
        String code = resp.getCode();
        isEqual(StatusCode.SUCCESS.getCode(), code, code, resp.getMsg());
    }

}
