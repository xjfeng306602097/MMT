package com.makro.mall.stat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/13
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogRecord extends AbstractLogRecord {

    /**
     * 日志内容
     */
    private Object data;


}
