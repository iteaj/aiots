package com.iteaj.security.config;

import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.auth.AccountAuthAction;
import com.iteaj.framework.spi.auth.AuthToken;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroAuthActionProxyPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {

    private final static String loginMethodName = "login";
    private final static String logoutMethodName = "logout";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AppAuthAction) {
            return new ProxyFactory(bean).getProxy(AppAuthAction.class, (MethodInterceptor) invocation -> {
                Method method = invocation.getMethod();
                Object invoke;
                if(Objects.equals(method.getName(), loginMethodName)) {
                    invoke = method.invoke(bean, invocation.getArguments());
                    if(bean instanceof AccountAuthAction) {
                        SecurityUtils.getSubject().login((AuthenticationToken) invoke);
                    }
                } else if(Objects.equals(method.getName(), logoutMethodName)) {
                    invoke = method.invoke(bean, invocation.getArguments());
                    SecurityUtils.getSubject().logout();
                } else {
                    invoke = method.invoke(bean, invocation.getArguments());
                }

                return invoke;
            });
        }

        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addBeanPostProcessor(this);
    }
}
