package com.makro.mall.common.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author xiaojunfeng
 * @description Makro分页
 * @date 2021/11/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MakroPage<T> extends Page<T> {

    private final long MAX_LIMIT = Long.MAX_VALUE;

    public MakroPage(long current, long size) {
        super(current, size);
        if (size == -1L) {
            this.current = 1L;
            this.size = MAX_LIMIT;
        }
    }

}
