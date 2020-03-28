package com.iteaj.framework.authority.tenant;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.utils.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import javax.servlet.ServletRequest;

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
        if(!ShiroUtils.isLogin() || ShiroUtils.isSuper()) return argument;

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
