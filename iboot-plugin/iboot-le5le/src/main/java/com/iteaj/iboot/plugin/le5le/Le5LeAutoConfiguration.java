package com.iteaj.iboot.plugin.le5le;

import com.iteaj.framework.ProfilesInclude;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@ProfilesInclude("le5le")
@ComponentScan({"com.iteaj.iboot.plugin.le5le"})
@MapperScan({"com.iteaj.iboot.plugin.le5le.mapper"})
@EnableConfigurationProperties(Le5leProperties.class)
public class Le5LeAutoConfiguration implements WebMvcConfigurer {

    private final Le5leProperties properties;
    private static final String SCADA_URL = "scada_url";

    public Le5LeAutoConfiguration(Le5leProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Order(value = 18)
    public OrderFilterChainDefinition le5leFilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addAnon("/scada/**")
                .addAnon("/view/**")
                .addAnon("/iot/le5le/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String locations = properties.getDeployDir().endsWith("/")
                ? properties.getDeployDir() : properties.getDeployDir() + "/";

        // 配置静态资源的访问路径和存放位置
        registry.addResourceHandler("/scada/**")
                .addResourceLocations("file:"+locations);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                final String contextPath = request.getContextPath();
                String requestURI = request.getRequestURI();
                if(requestURI.startsWith(contextPath + "/view")) {
                    final String scadaUrl = getScadaUrlCookie(request);
                    if(scadaUrl != null) {
                        if(request.getParameter("data") != null) {
                            final String forwardUri = scadaUrl.substring(requestURI.indexOf(contextPath) + contextPath.length());

                            request.getRequestDispatcher(forwardUri + "?" + request.getQueryString()).forward(request, response); return false;
                        } else {
                            final String forwardUri = scadaUrl.substring(scadaUrl.indexOf("/scada"), scadaUrl.indexOf("/view"));

                            requestURI = requestURI.substring(contextPath.length());
                            final String url = forwardUri + requestURI;
                            request.getRequestDispatcher(url).forward(request, response); return false;
                        }
                    }
                } else if(requestURI.startsWith(contextPath + "/scada")) {
                    if(requestURI.contains("/view/index.html")) {
                        final Cookie cookie = new Cookie("scada_url", requestURI);
                        cookie.setPath("/view");
                        response.addCookie(cookie);
                    }
                }

                return true;
            }

            private String getScadaUrlCookie(HttpServletRequest request) {
                final Cookie[] cookies = request.getCookies();
                if(cookies != null && cookies.length > 0) {
                    for (int i = 0; i < cookies.length; i++) {
                        final Cookie cookie = cookies[i];
                        if(cookie.getName().equals(SCADA_URL)) {
                            return cookie.getValue();
                        }
                    }
                }
                return null;
            }
        }).addPathPatterns("/view/**", "/scada/*/view/index.html");
    }
}
