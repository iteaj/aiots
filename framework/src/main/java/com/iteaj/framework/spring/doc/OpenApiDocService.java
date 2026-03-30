package com.iteaj.framework.spring.doc;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.webmvc.api.OpenApiResource;

import java.util.Optional;

/**
 * Open Api文档服务, 用于获取各个模块的{@link io.swagger.v3.oas.models.OpenAPI}文档
 * @see io.swagger.v3.oas.models.OpenAPI
 */
public interface OpenApiDocService {

    /**
     * 获取指定某块的OpenAPI文档
     * @param group 组标识
     * @return OpenAPI对象
     */
    Optional<OpenAPI> getOpenApi(String group);

    OpenApiResource register(String group, MultipleOpenApiWebMvcResourceExt.OpenApiWebMvcResourceWrapper resourceWrapper);
}
