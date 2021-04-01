package com.iteaj.security.config;

import com.iteaj.framework.Proxy.TargetInvocationHandler;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.security.shiro.ShiroAuthTokenAdapter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * create time: 2021/3/27
 *  对{@link WebAuthAction}进行登录和注销的代理
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroAuthActionProxyPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {

    private final static String loginMethodName = "login";
    private final static String logoutMethodName = "logout";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof WebAuthAction) {
            return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{WebAuthAction.class}, new TargetInvocationHandler() {
                @Override
                public Object getTarget() {
                    return bean;
                }

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Object invoke;
                    if(Objects.equals(method.getName(), loginMethodName)) {
                        try {
                            invoke = method.invoke(bean, args);
                            if(null == invoke) {
                                throw new AuthException("认证失败");
                            }

                            // shiro 登录
                            SecurityUtils.getSubject().login(new
                                    ShiroAuthTokenAdapter((AuthToken) invoke));
                        } catch (AuthenticationException e) {
                            throw new AuthException(e.getMessage(), e);
                        }
                    } else if(Objects.equals(method.getName(), logoutMethodName)) {
                        // 先验证是否已经认证
                        if(SecurityUtils.getSubject().isAuthenticated()) {
                            invoke = method.invoke(bean, args);
                            SecurityUtils.getSubject().logout();
                        } else {
                            throw new AuthException("未认证");
                        }
                    } else {
                        invoke = method.invoke(bean, args);
                    }

                    return invoke;
                }
            });

        }

        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addBeanPostProcessor(this);
    }
}
