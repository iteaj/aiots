package com.iteaj.security.shiro;

import com.google.common.eventbus.Subscribe;
import com.iteaj.framework.spi.admin.LoggerEntity;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.event.FrameworkListener;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.StringUtils;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * create time: 2020/4/21
 *  记录客户端操作日志
 * @see AuthorizingLogMethodInterceptor
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthorLogListener implements FrameworkListener<ShiroAuthorEvent> {

    private LoggerService loggerService;

    public ShiroAuthorLogListener(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public ShiroAuthorLogListener setLoggerService(LoggerService loggerService) {
        this.loggerService = loggerService;
        return this;
    }

    @Override
    @Subscribe
    public void onEvent(ShiroAuthorEvent event) {
        String[] perms = getPerms(event.getMethod());
        String errMsg = StringUtils.hasText(event.getErrMsg())
                ? event.getErrMsg() : event.getType().desc;

        String requestURI = null;
        if(event.getRequest() != null) {
            requestURI = event.getRequest().getRequestURI();
        }

        LoggerEntity loggerEntity = new LoggerEntity(requestURI
                , event.getArgs(), event.getSource(), event.getType())
                .setShortName(getShortName(event.getMethod()))
                .setPerms(perms).setId("Shiro").setErrMsg(errMsg)
                .setOperaTime(event.getOperaTimeMillis());

        this.loggerService.record(loggerEntity);
    }

    private String[] getPerms(Method method) {
        if(method != null) {
            RequiresPermissions permissions =
                    method.getDeclaredAnnotation(RequiresPermissions.class);

            if(permissions!=null) {
                return permissions.value();
            }
        }

        return new String[0];
    }

    private String getShortName(Method method) {
        if(null == method) return "";

        Class<?> declaringClass = method.getDeclaringClass();
        String methodArgs = Stream.of(method.getParameterTypes()).map(obj -> obj.getSimpleName())
                .reduce((i, j) -> i + ',' + j).orElse("");

        return String.format("%s.%s(%s)", declaringClass.getSimpleName()
                , method.getName(), methodArgs);
    }
}
