package com.iteaj.framework.shiro;

import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.admin.LoggerType;
import com.iteaj.framework.spi.event.EventUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.subject.WebSubject;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
public class AuthorizingLogMethodInterceptor extends AopAllianceAnnotationsAuthorizingMethodInterceptor {

    @Autowired(required = false)
    private LoggerService loggerService;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        LoggerType status = LoggerType.Success;
        WebSubject subject = (WebSubject) SecurityUtils.getSubject();
        Object source = subject.getPrincipal();
        long startTime = System.currentTimeMillis();
        try {
            return super.invoke(methodInvocation);
        } catch (UnauthorizedException ae) { // 无权限访问
            status = LoggerType.UnAuthor;
            throw ae;
        } catch (Throwable throwable) {
            status = LoggerType.Error;
            status.desc = throwable.getMessage();
            throw throwable;
        } finally {
            ShiroHttpServletRequest servletRequest = (ShiroHttpServletRequest) subject.getServletRequest();

            long operaTime = System.currentTimeMillis()-startTime;
            Method method = methodInvocation.getMethod();
            Object[] arguments = methodInvocation.getArguments();
            EventUtils.publish(new ShiroAuthorEvent(source, servletRequest, status)
                    .setMethod(method).setArgs(arguments)
                    .setOperaTimeMillis(operaTime));
        }
    }
}
