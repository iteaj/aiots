package com.iteaj.framework.mybatis.tenant;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.admin.SecurityUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * create time: 2020/3/6
 *
 * @author iteaj
 * @since 1.0
 */
public class TenantHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private HandlerMethodArgumentResolver methodProcessor;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(BaseEntity.class.isAssignableFrom(parameter.getParameterType())) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object argument = methodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        // 未登录或者是超级管理员, 多不做租户处理
        if(!SecurityUtils.isLogin() || SecurityUtils.isSuper()) return argument;

        BaseEntity baseEntity = null;
        if(null != argument) {
            baseEntity = (BaseEntity)argument;
        }

        return argument;
    }

    public TenantHandlerMethodArgumentResolver setMethodProcessor(HandlerMethodArgumentResolver methodProcessor) {
        this.methodProcessor = methodProcessor;
        return this;
    }
}
