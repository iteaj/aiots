package com.iteaj.listener;

import com.iteaj.framework.spi.admin.LoggerEvent;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.event.FrameworkListener;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.lang.reflect.Method;

/**
 * create time: 2020/4/21
 *  记录客户端操作日志
 * @see com.iteaj.framework.shiro.ShiroFilterLogFactoryBean
 * @author iteaj
 * @since 1.0
 */
public class AccessLogListener implements FrameworkListener<LoggerEvent> {

    private LoggerService loggerService;

    public AccessLogListener(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public AccessLogListener setLoggerService(LoggerService loggerService) {
        this.loggerService = loggerService;
        return this;
    }

    @Override
    public void onFrameworkEvent(LoggerEvent event) {

        this.loggerService.record(event);
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
}
