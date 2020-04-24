package com.iteaj.admin.service;

import com.iteaj.admin.entity.Admin;
import com.iteaj.admin.entity.Menu;
import com.iteaj.admin.entity.OperaLog;
import com.iteaj.izone.api.LoggerEntity;
import com.iteaj.izone.api.LoggerService;
import com.iteaj.izone.api.LoggerType;

/**
 * create time: 2020/4/21
 *  管理端操作日志记录服务
 * @author iteaj
 * @since 1.0
 */
public class AdminLoggerService implements LoggerService {

    private final IMenuService menuService;
    private final IOperaLogService operaLogService;

    public AdminLoggerService(IOperaLogService operaLogService, IMenuService menuService) {
        this.operaLogService = operaLogService;
        this.menuService = menuService;
    }

    @Override
    public void record(LoggerEntity entity) {
        OperaLog operaLog = new OperaLog()
                .setStatus(entity.getType().name()).setErrMsg(entity.getErrMsg())
                .setMillis(entity.getOperaTime()).setUrl(entity.getUrl())
                .setShortName(entity.getShortName());

        if(entity.getSource() instanceof Admin) {
            Admin source = (Admin) entity.getSource();
            operaLog.setUserId((source).getId())
                    .setUserName(source.getName());
        }

        if(entity.getType() == LoggerType.Login) {
            operaLog.setType(entity.getType().name());
            operaLog.setTitle("系统登录");
            operaLogService.insert(operaLog);
        } else if(entity.getType() == LoggerType.Logout) {
            operaLog.setType(entity.getType().name());
            operaLog.setTitle("注销登录");
            operaLogService.insert(operaLog);
        } else {
            Menu menu = getMenu(entity.getUrl());
            if(menu != null && menu.getLog()) {
                operaLog.setTitle(menu.getLogDesc())
                        .setType(menu.getPermType());

                operaLogService.insert(operaLog);
            }
        }
    }

    private Menu getMenu(String url) {
        return this.menuService.getByUrl(url);
    }
}
