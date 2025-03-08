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

    public Le5LeAutoConfiguration(Le5leProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Order(value = 18)
    public OrderFilterChainDefinition le5leFilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addAnon("/scada/**")
                .addAnon("/view/**");
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
                String requestURI = request.getRequestURI();
                if(requestURI.contains("/view")) {
                    final String referer = request.getHeader("referer");
                    if(requestURI.contains("/view") && "2d".equals(request.getParameter("data"))) {
                        response.sendRedirect(referer + "?data=2d"); return false;
                    } else if(requestURI.contains("/view/js") || requestURI.contains("/view/css")) {
                        final String[] split = referer.split("/");
                        final String[] result = new String[split.length - 5];
                        System.arraycopy(split, 3, result, 0, result.length);

                        requestURI = requestURI.substring(request.getContextPath().length());
                        final String url = "/" + Arrays.stream(result).collect(Collectors.joining("/")) + requestURI;
                        request.getRequestDispatcher(url).forward(request, response); return false;
                    }
                }

                return true;
            }
        }).addPathPatterns("/view/**");
    }
}
