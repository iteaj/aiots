package com.iteaj.dev.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.ApiOrderExtension;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@EnableKnife4j
@Configuration
@EnableSwagger2
public class Knife4jConfiguration {

    /**
     * 开放下面的路径, 不能被权限管理器拦截
     * @return
     */
    @Bean
    public OrderFilterChainDefinition knife4jOrderFilterChain() {
        return new OrderFilterChainDefinition()
                .addPathDefinition("/doc.html", "anon")
                .addPathDefinition("/webjars/**", "anon")
                .addPathDefinition("/swagger-resources", "anon")
                .addPathDefinition("/v2/api-docs-ext", "anon")
                .addPathDefinition("/v2/api-docs", "anon");
    }

    /**
     * 核心模块Api
     * @return
     */
    @Bean
    public Docket coreRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("系统核心 Api接口文档管理"
                        , 1, "包含用户管理、角色管理、系统设置、权限管理等核心系统功能 (admin)"))
                .groupName("系统管理模块")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.iteaj.core.controller"))
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 开发模块Api
     * @return
     */
    @Bean
    public Docket devRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("系统开发模块 Api接口文档管理", 10
                        , "包含knife4j文档管理、代码生成器等开发环境需要用到的开发功能 (develop)"))
                .groupName("开发模块")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.iteaj.dev.controller"))
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(String title, int order, String desc) {
        return new ApiInfoBuilder()
                .title(title)
                .version("1.0")
                .description(desc)
                .extensions(Arrays.asList(new ApiOrderExtension(order)))
                .contact(new Contact("厦门由创源智能科技"
                        , "http://www.iteaj.com", "iteaj@outlook.com"))
                .build();
    }
}
