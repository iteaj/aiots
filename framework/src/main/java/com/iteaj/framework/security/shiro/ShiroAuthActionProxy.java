package com.iteaj.framework.security.shiro;

import com.iteaj.framework.Proxy.TargetInvocationHandler;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.SecurityException;
import com.iteaj.framework.spi.auth.WebAuthAction;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import static com.iteaj.framework.consts.CoreConst.EXEC_EXCEPTION_STATUS;

/**
 * create time: 2021/3/27
 *  对{@link WebAuthAction}进行登录和注销的代理
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthActionProxy implements BeanPostProcessor, BeanFactoryPostProcessor {

    private final static String loginMethodName = "login";
    private final static String logoutMethodName = "logout";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof WebAuthAction) {
            return Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{WebAuthAction.class}, new TargetInvocationHandler() {

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
                            if(invoke instanceof AuthToken) {
                                // shiro 登录
                                SecurityUtils.getSubject().login(new ShiroTokenDelegate((AuthToken) invoke));
                            } else {
                                throw new SecurityException("认证失败");
                            }
                        } catch (InvocationTargetException e) {
                            final Throwable target = e.getTargetException();
                            if(target instanceof ShiroException) {
                                ShiroUtil.setRequestAttr(EXEC_EXCEPTION_STATUS, e.getMessage());
                                throw new SecurityException("认证失败", target);
                            } else if(target instanceof SecurityException) {
                                ShiroUtil.setRequestAttr(EXEC_EXCEPTION_STATUS, e.getMessage());
                                throw target;
                            } else {
                                ShiroUtil.setRequestAttr(EXEC_EXCEPTION_STATUS, "认证失败");
                                throw new SecurityException("认证失败", target);
                            }
                        } catch (Exception e) {
                            ShiroUtil.setRequestAttr(EXEC_EXCEPTION_STATUS, "认证失败");
                            throw new SecurityException("认证失败", e);
                        }
                    } else if(Objects.equals(method.getName(), logoutMethodName)) {
                        invoke = method.invoke(bean, args);

                        // 发布注销事件
                        SecurityUtils.getSubject().logout();
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
