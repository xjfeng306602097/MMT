package com.makro.mall.common.mybatis.wrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/11
 */
public class MarkoLambdaUpdateWrapper<T> extends LambdaUpdateWrapper<T> {

    public MarkoLambdaUpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        super((T) null);
    }

    public MarkoLambdaUpdateWrapper(T entity) {
        super(entity);
    }

    public MarkoLambdaUpdateWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * 指定列自增
     *
     * @param column 列
     * @param value  增长值
     */
    public MarkoLambdaUpdateWrapper<T> incrField(SFunction<T, ?> column, Object value) {
        String sql = formatParam(null, value);
        String columnToString = columnToString(column);
        String format = String.format("%s = %s + %s", columnToString, columnToString, sql);
        setSql(format);
        return this;
    }

    /**
     * 指定列自减
     *
     * @param column 列
     * @param value  减少值
     */
    public MarkoLambdaUpdateWrapper<T> descField(SFunction<T, ?> column, Object value) {
        String sql = formatParam(null, value);
        String columnToString = columnToString(column);
        String format = String.format("%s = %s - %s", columnToString, columnToString, sql);
        setSql(format);
        return this;
    }


}
