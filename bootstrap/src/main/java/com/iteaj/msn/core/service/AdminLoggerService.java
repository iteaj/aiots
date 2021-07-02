package com.iteaj.msn.core.service;

import com.iteaj.framework.spi.admin.*;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.entity.AccessLog;
import com.iteaj.util.module.mvc.orm.Entity;

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
    public void record(AccessLogger entity) {
        AccessLog accessLog = new AccessLog().setErrMsg(entity.getRemark())
                .setIp(entity.getIp()).setUrl(entity.getUrl())
                .setMillis(entity.getExecTime()).setStatus(entity.isStatus());

        if(SecurityUtils.isLogin()) {
            final Admin admin = SecurityUtils.getUser();
            accessLog.setUserId(admin.getId()).setUserName(admin.getName());
        }

        final Menu menu = getMenu(entity.getUrl());
        if(menu != null) {
            accessLog.setTitle(menu.getName());
            final Long pid = menu.getPid();

            // 获取所属模块
            if(pid != null && pid > 0) {
                this.menuService.getById(pid).ofNullable().ifPresent(parent -> {
                    accessLog.setMsn(parent.getName());
                });
            }
        }

        operaLogService.insert(accessLog);
    }

    @Override
    public LoggerMenu getLoggerMenu(String url) {
        return getMenu(url);
    }

    private Menu getMenu(String url) {
        return this.menuService.getByUrl(url);
    }
}
