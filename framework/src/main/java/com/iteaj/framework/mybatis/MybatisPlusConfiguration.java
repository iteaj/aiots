package com.iteaj.framework.mybatis;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * create time: 2020/3/29
 *
 * @author iteaj
 * @since 1.0
 */
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
public class MybatisPlusConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return getPaginationInterceptor();
    }

    protected PaginationInterceptor getPaginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        TenantParseFilter tenantParseFilter = new TenantParseFilter();
//        paginationInterceptor.setSqlParserList(tenantParseFilter.getSqlParserList());
//        paginationInterceptor.setSqlParserFilter(tenantParseFilter.getSqlParserFilter());
        return paginationInterceptor;
    }
}
