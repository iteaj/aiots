package com.iteaj.iboot.plugin.swagger;

import com.iteaj.framework.plugin.Plugin;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

public class Knife4jAutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered, Plugin {

    @Autowired
    private ServerProperties serverProperties;

    @Bean
    public OrderFilterChainDefinition swaggerFilterChainDefinition() {
        return new OrderFilterChainDefinition().addAnon("/swagger/**", "/swagger-resources/**", "/swagger-instance/**"
                , "/doc.html", "/webjars/**", "/openapi/**", "/v3/api-docs/**");
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        event.getEnvironment().addActiveProfile("knife4j");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }

    @Override
    public String getPluginName() {
        return "Knife4j";
    }

    @Override
    public String getPluginDesc() {
        return "使用Smart-doc和knife4j框架实现的文档";
    }

    @Override
    public void banner() {
        Integer port = serverProperties.getPort();
        String contextPath = serverProperties.getServlet().getContextPath();
        System.out.println(String.format("------------------------------------------------- 启用knife4j api文档插件 -----------------------------------------------\r\n" +
                        "                              knife4j访问地址：http://127.0.0.1:%s%s%s)                                           \r\n" +
                        "                       smart-doc访问地址：http://127.0.0.1:%s%s%s)                              \r\n" +
                        "-----------------------------------------------------------------------------------------------------------------------"
                , port + "", contextPath, "/doc.html", port + "", contextPath, "/static/doc/index.html"));
    }

}
