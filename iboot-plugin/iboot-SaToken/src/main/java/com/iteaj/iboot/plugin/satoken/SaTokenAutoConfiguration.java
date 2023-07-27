package com.iteaj.iboot.plugin.satoken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.iteaj.framework.captcha.CaptchaService;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.*;
import com.iteaj.iboot.plugin.satoken.impl.SaTokenSecurityService;
import com.iteaj.iboot.plugin.satoken.impl.StpInterfaceImpl;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SaTokenAutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered, WebMvcConfigurer {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        event.getEnvironment().addActiveProfile("satoken");
    }

    @Bean
    public StpInterface stpInterfaceImpl(AuthorizationService authorizationService) {
        return new StpInterfaceImpl(authorizationService);
    }

    @Bean
    public SecurityService securityService(CaptchaService captchaService
            , AuthorizationService authorizationService, AuthenticationService authenticationService) {
        return new SaTokenSecurityService(captchaService, authorizationService, authenticationService);
    }

    /**
     * 增加权限校验拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnProperty(value = "framework.cluster", havingValue = "true")
    @ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedisJackson();
    }

    @Bean
    public SaServletFilter getSaServletFilter(List<OrderFilterChainDefinition> definitions) {
        SaServletFilter servletFilter = new SaServletFilter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest servletRequest = (HttpServletRequest) request;

                // 解析浏览器的UserAgent并将放入request中
                UserAgent userAgent = UserAgent.parseUserAgentString(servletRequest.getHeader("user-agent"));
                servletRequest.setAttribute(CoreConst.WEB_USER_AGENT, userAgent);

                super.doFilter(request, response, chain);
            }
        };
        definitions.forEach(item -> {
            item.getFilterChainMap().forEach((key, value) -> {
                if("anon".endsWith(value)) {
                    servletFilter.addExclude(key);
                } else {
                    servletFilter.addInclude(key);
                }
            });
        });

        return servletFilter
                .setAuth(obj -> StpUtil.checkLogin())
                .setError(e -> {
                    SaHolder.getResponse()
                            .setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                            .setHeader("Content-Type", "application/json;charset=UTF-8");
                    return JSONUtil.toJsonStr(Result.fail("未认证"));
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
