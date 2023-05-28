package com.makro.mall.stat.pojo.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/13
 */
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LogRecord extends AbstractLogRecord {

    /**
     * 日志内容
     */
    private Object data;


}
