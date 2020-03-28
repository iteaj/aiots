package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.authority.tenant.TenantHandlerMethodArgumentResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.util.List;

/**
 * create time: 2020/3/6
 *
 * @author iteaj
 * @since 1.0
 */
//@Configuration
@ConditionalOnWebApplication
public class WebFrameworkConfiguration implements WebMvcConfigurer, ApplicationListener<ContextRefreshedEvent> {

    private TenantHandlerMethodArgumentResolver argumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(argumentResolver = new TenantHandlerMethodArgumentResolver());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(null != argumentResolver) {
            RequestMappingHandlerAdapter handlerAdapter = event.getApplicationContext().getBean(RequestMappingHandlerAdapter.class);
            HandlerMethodArgumentResolver argumentResolver = handlerAdapter.getArgumentResolvers().stream().filter(resolver ->
                    resolver instanceof ServletModelAttributeMethodProcessor).findFirst().get();

            this.argumentResolver.setMethodProcessor(argumentResolver);
        }
    }
}
