package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spring.doc.DefaultOpenApiDocServiceImpl;
import com.iteaj.framework.spring.doc.MultipleOpenApiWebMvcResourceExt;
import com.iteaj.framework.spring.doc.OpenApiDocService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.List;

import static org.springdoc.core.utils.Constants.SPRINGDOC_USE_MANAGEMENT_PORT;

/**
 * 提供open api规范的文档支持
 */
public class OpenApiAutoConfiguration {

    @Bean
    @Order(value = 1800)
    @Profile("dev")
    public OrderFilterChainDefinition openApiFilterChainDefinition() {
        return new OrderFilterChainDefinition().addAnon("/swagger-ui/**", "/v3/api-docs/**");
    }

    @Bean
    public OpenAPI aiotsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("aiots物联网项目的Open Api文档").version("3.2.0")
                        .description("aiots是基于Spring Boot3.5+的物联网项目，提供设备管理、数据采集、设备控制、数据存储、数据处理、数据可视化的能力！基于SpringDoc实现的Open Api文档"));
    }

    @Bean
//    @ConditionalOnProperty(name = SPRINGDOC_USE_MANAGEMENT_PORT, havingValue = "false", matchIfMissing = true)
    public MultipleOpenApiWebMvcResource multipleOpenApiResource(List<GroupedOpenApi> groupedOpenApis,
          ObjectFactory<OpenAPIService> defaultOpenAPIBuilder, AbstractRequestService requestBuilder,
          GenericResponseService responseBuilder, OperationService operationParser,
          SpringDocConfigProperties springDocConfigProperties, OpenApiDocService openApiDocService,
          SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
        return new MultipleOpenApiWebMvcResourceExt(groupedOpenApis,
                defaultOpenAPIBuilder, requestBuilder,
                responseBuilder, operationParser,
                springDocConfigProperties, springDocProviders
                , springDocCustomizers, openApiDocService);
    }

    @Bean
    public OpenApiDocService openApiDocService() {
        return new DefaultOpenApiDocServiceImpl();
    }
}
