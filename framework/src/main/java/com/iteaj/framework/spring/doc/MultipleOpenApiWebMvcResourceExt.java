package com.iteaj.framework.spring.doc;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource;
import org.springdoc.webmvc.api.OpenApiActuatorResource;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.springdoc.core.utils.Constants.ACTUATOR_DEFAULT_GROUP;

public class MultipleOpenApiWebMvcResourceExt extends MultipleOpenApiWebMvcResource {

    private final OpenApiDocService openApiDocService;

    /**
     * Instantiates a new Multiple open api resource.
     *
     * @param groupedOpenApis           the grouped open apis
     * @param defaultOpenAPIBuilder     the default open api builder
     * @param requestBuilder            the request builder
     * @param responseBuilder           the response builder
     * @param operationParser           the operation parser
     * @param springDocConfigProperties the spring doc config properties
     * @param springDocProviders        the spring doc providers
     * @param springDocCustomizers      the spring doc customizers
     */
    public MultipleOpenApiWebMvcResourceExt(List<GroupedOpenApi> groupedOpenApis, ObjectFactory<OpenAPIService> defaultOpenAPIBuilder, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers, OpenApiDocService openApiDocService) {
        super(groupedOpenApis, defaultOpenAPIBuilder, requestBuilder, responseBuilder, operationParser, springDocConfigProperties, springDocProviders, springDocCustomizers);
        this.openApiDocService = openApiDocService;
    }

    @Override
    protected OpenApiResource buildOpenApiResource(GroupedOpenApi item) {
        if (!springDocConfigProperties.isUseManagementPort() && !ACTUATOR_DEFAULT_GROUP.equals(item.getGroup())) {
            OpenApiWebMvcResourceWrapper webMvcResourceWrapper = new OpenApiWebMvcResourceWrapper(item.getGroup(),
                    defaultOpenAPIBuilder,
                    requestBuilder,
                    responseBuilder,
                    operationParser,
                    springDocConfigProperties, springDocProviders,
                    new SpringDocCustomizers(Optional.of(item.getOpenApiCustomizers()), Optional.of(item.getOperationCustomizers()),
                            Optional.of(item.getRouterOperationCustomizers()), Optional.of(item.getOpenApiMethodFilters()), Optional.empty(), Optional.empty())

            );

            return openApiDocService.register(item.getGroup(), webMvcResourceWrapper);
        } else
            return new OpenApiActuatorResource(item.getGroup(),
                    defaultOpenAPIBuilder,
                    requestBuilder,
                    responseBuilder,
                    operationParser,
                    springDocConfigProperties, springDocProviders,
                    new SpringDocCustomizers(Optional.of(item.getOpenApiCustomizers()), Optional.of(item.getOperationCustomizers()),
                            Optional.of(item.getRouterOperationCustomizers()), Optional.of(item.getOpenApiMethodFilters()),Optional.empty(),Optional.empty())
            );
    }

    public class OpenApiWebMvcResourceWrapper extends OpenApiWebMvcResource {

        public OpenApiWebMvcResourceWrapper(String groupName, ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
            super(groupName, openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, springDocConfigProperties, springDocProviders, springDocCustomizers);
        }

        public OpenApiWebMvcResourceWrapper(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
            super(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, springDocConfigProperties, springDocProviders, springDocCustomizers);
        }

        @Override
        public OpenAPI getOpenApi(Locale locale) {
            return super.getOpenApi(locale);
        }
    }
}
