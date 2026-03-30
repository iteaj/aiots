package com.iteaj.framework.spring.doc;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.webmvc.api.*;

import java.util.*;

public class DefaultOpenApiDocServiceImpl implements OpenApiDocService {

    protected Map<String, MultipleOpenApiWebMvcResourceExt.OpenApiWebMvcResourceWrapper> resources = new HashMap<>(8);

    public Optional<OpenAPI> getOpenApi(String group) {
        MultipleOpenApiWebMvcResourceExt.OpenApiWebMvcResourceWrapper resourceWrapper = resources.get(group);
        return Optional.ofNullable(resourceWrapper == null ? null : resourceWrapper.getOpenApi(Locale.getDefault()));
    }

    @Override
    public OpenApiResource register(String group, MultipleOpenApiWebMvcResourceExt.OpenApiWebMvcResourceWrapper resourceWrapper) {
        resources.put(group, resourceWrapper);
        return resourceWrapper;
    }

}
