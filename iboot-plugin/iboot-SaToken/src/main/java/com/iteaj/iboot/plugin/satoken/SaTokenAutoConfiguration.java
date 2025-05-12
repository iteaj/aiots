package com.iteaj.iboot.plugin.satoken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisFastjson2;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.iteaj.framework.ProfilesInclude;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.captcha.CaptchaService;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.*;
import com.iteaj.framework.spi.open.OpenApiUtil;
import com.iteaj.framework.spring.condition.ConditionalOnCluster;
import com.iteaj.framework.spring.condition.ConditionalOnRedis;
import com.iteaj.iboot.plugin.satoken.impl.SaTokenSecurityService;
import com.iteaj.iboot.plugin.satoken.impl.StpInterfaceImpl;
import com.iteaj.iboot.plugin.satoken.listener.SaTokenOnlineListener;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@ProfilesInclude("satoken")
public class SaTokenAutoConfiguration implements WebMvcConfigurer {

    private final List<OrderFilterChainDefinition> definitions;

    public SaTokenAutoConfiguration(List<OrderFilterChainDefinition> definitions) {
        this.definitions = definitions;
    }

    @Bean
    public SaTokenOnlineListener saTokenOnlineListener() {
        return new SaTokenOnlineListener();
    }

    @Bean
    public StpInterface stpInterfaceImpl(AuthorizationService authorizationService) {
        return new StpInterfaceImpl(authorizationService);
    }

    @Bean
    public SecurityService securityService(CaptchaService captchaService, FrameworkProperties properties
            , AuthorizationService authorizationService, AuthenticationService authenticationService) {
        return new SaTokenSecurityService(captchaService, properties, authorizationService, authenticationService);
    }

    /**
     * 增加权限校验拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/**");
        definitions.forEach(item -> {
            item.getFilterChainMap().forEach((key, value) -> {
                if("anon".endsWith(value)) {
                    registration.excludePathPatterns(key);
                }
            });
        });
    }

    @Bean
    @ConditionalOnRedis
    @ConditionalOnCluster
//    @ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedisFastjson2();
    }

    @Bean
    public SaServletFilter getSaServletFilter(List<OrderFilterChainDefinition> definitions) {
        return new OrderServletFilter(definitions)
                .setAuth(obj -> StpUtil.checkLogin())
                .setError(e -> {
                    SaHolder.getResponse()
                            .setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                            .setHeader("Content-Type", "application/json;charset=UTF-8");
                    return JSON.toJSONString(Result.fail(HttpServletResponse.SC_UNAUTHORIZED, "未认证"));
                });
    }

    public static class OrderServletFilter extends SaServletFilter implements Ordered {

        public OrderServletFilter(List<OrderFilterChainDefinition> definitions) {
            if(definitions != null) {
                definitions.forEach(item -> {
                    item.getFilterChainMap().forEach((key, value) -> {
                        if("anon".endsWith(value)) {
                            this.addExclude(key);
                        } else {
                            this.addInclude(key);
                        }
                    });
                });
            }
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if(OpenApiUtil.isOpenRequest(request)) {
                chain.doFilter(request, response);
            } else {
                HttpServletRequest servletRequest = (HttpServletRequest) request;

                // 解析浏览器的UserAgent并将放入request中
                UserAgent userAgent = UserAgent.parseUserAgentString(servletRequest.getHeader("user-agent"));
                servletRequest.setAttribute(CoreConst.WEB_USER_AGENT, userAgent);

                super.doFilter(request, response, chain);
            }
        }

        @Override
        public int getOrder() {
            return 8888;
        }
    }
}
