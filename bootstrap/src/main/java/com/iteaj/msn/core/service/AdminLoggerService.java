package com.iteaj.msn.core.service;

import com.iteaj.framework.spi.admin.LoggerMenu;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.entity.AccessLog;
import com.iteaj.framework.spi.admin.LoggerEvent;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.admin.LoggerType;

/**
 * create time: 2020/4/21
 *  管理端操作日志记录服务
 * @author iteaj
 * @since 1.0
 */
public class AdminLoggerService implements LoggerService {

    private final IMenuService menuService;
    private final IAccessLogService operaLogService;

    public AdminLoggerService(IAccessLogService operaLogService, IMenuService menuService) {
        this.operaLogService = operaLogService;
        this.menuService = menuService;
    }

    @Override
    public void record(LoggerEvent entity) {
        AccessLog accessLog = new AccessLog()
                .setStatus(entity.getType().name()).setErrMsg(entity.getErrMsg())
                .setMillis(entity.getOperaTime()).setUrl(entity.getUrl());

        if(entity.getSource() instanceof Admin) {
            Admin source = (Admin) entity.getSource();
            accessLog.setUserId((source).getId())
                    .setUserName(source.getName());
        }

        if(entity.getType() == LoggerType.Login) {
            accessLog.setType(entity.getType().name());
            accessLog.setTitle("系统登录");
            operaLogService.insert(accessLog);
        } else if(entity.getType() == LoggerType.Logout) {
            accessLog.setType(entity.getType().name());
            accessLog.setTitle("注销登录");
            operaLogService.insert(accessLog);
        } else if(entity.getType() == LoggerType.UnAuthor) {
            accessLog.setTitle("访问未授权的页面");
            accessLog.setType(LoggerType.UnAuthor.name());
        } else if(entity.getType() == LoggerType.UnAuth) {
            accessLog.setType(entity.getType().name());
            accessLog.setTitle("访问需认证的页面");
        } else {
            Menu menu = getMenu(entity.getUrl());
        }
    }

    @Override
    public LoggerMenu getLoggerMenu(String url) {
        return null;
    }

    private Menu getMenu(String url) {
        return this.menuService.getByUrl(url);
    }
}
