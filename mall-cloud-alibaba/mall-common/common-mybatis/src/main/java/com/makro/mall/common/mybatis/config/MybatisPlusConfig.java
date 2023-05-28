package com.makro.mall.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.makro.mall.common.constants.DriverConstants;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.mybatis.handler.IntegerArrayJsonTypeHandler;
import com.makro.mall.common.mybatis.handler.LongArrayJsonTypeHandler;
import com.makro.mall.common.mybatis.handler.MyMetaObjectHandler;
import com.makro.mall.common.mybatis.handler.StringArrayJsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author xiaojunfeng
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig implements DriverConstants {

    @Value("${spring.datasource.driver-class-name:oracle.jdbc.driver.OracleDriver}")
    private String driverClassName;

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        switch (driverClassName) {
            case ORACLE:
                interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE_12C));
                break;
            case CLICK_HOUSE:
                interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.CLICK_HOUSE));
                break;
            default:
                throw new BusinessException(StatusCode.DRIVER_CLASS_NAME_NOT_FOUND);
        }
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 全局注册自定义TypeHandler
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            typeHandlerRegistry.register(String[].class, JdbcType.OTHER, StringArrayJsonTypeHandler.class);
            typeHandlerRegistry.register(Long[].class, JdbcType.OTHER, LongArrayJsonTypeHandler.class);
            typeHandlerRegistry.register(Integer[].class, JdbcType.OTHER, IntegerArrayJsonTypeHandler.class);
        };
    }

    /**
     * 自动填充数据库创建人、创建时间、更新人、更新时间
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        return globalConfig;
    }

}
