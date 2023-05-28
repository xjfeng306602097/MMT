package com.makro.mall.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.makro.mall.common.mybatis.util.JwtUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 字段自动填充
 *
 * @link https://mp.baomidou.com/guide/auto-fill-metainfo.html
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATOR = "creator";

    private static final String LAST_UPDATER = "lastUpdater";


    /**
     * 新增填充创建时间
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime::now, LocalDateTime.class);
        if (metaObject.hasGetter(CREATOR) && metaObject.getValue(CREATOR) == null) {
            this.strictInsertFill(metaObject, CREATOR, String.class,
                    Optional.ofNullable(JwtUtils.getUsername()).orElse("empty"));
        }
        if (metaObject.hasGetter(LAST_UPDATER) && metaObject.getValue(LAST_UPDATER) == null) {
            this.strictUpdateFill(metaObject, LAST_UPDATER, String.class,
                    Optional.ofNullable(JwtUtils.getUsername()).orElse("empty"));
        }
    }

    /**
     * 更新填充更新时间
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "lastUpdater", String.class,
                Optional.ofNullable(JwtUtils.getUsername()).orElse("empty"));
    }

}
