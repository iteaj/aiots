package com.iteaj.framework.spring.boot.config;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.iteaj.framework.api.AuthorizingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.LiveBeansView;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({FrameworkProperties.class})
public class FrameworkConfiguration implements EnvironmentAware {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private AuthorizingService authorizingService;

    @Bean
    @ConditionalOnClass(name = "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration")
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

    @Override
    public void setEnvironment(Environment environment) {
        String property = environment.getProperty("spring.liveBeansView.mbeanDomain");
        String applicationName = environment.getProperty("spring.application.name");
        System.setProperty(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME, applicationName);
    }
}

