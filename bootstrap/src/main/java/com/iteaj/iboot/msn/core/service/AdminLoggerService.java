package com.iteaj.iboot.msn.core.service;

import com.iteaj.framework.logger.AccessLogger;
import com.iteaj.framework.logger.LoggerService;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.iboot.msn.core.entity.AccessLog;
import com.iteaj.iboot.msn.core.entity.Admin;
import com.iteaj.iboot.msn.core.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * create time: 2020/4/21
 *  管理端操作日志记录服务
 * @author iteaj
 * @since 1.0
 */
public class AdminLoggerService implements LoggerService {

    @Autowired
    private IMenuService menuService;
    @Autowired
    private IAccessLogService operaLogService;

    @Async
    @Override
    public void record(AccessLogger entity) {
        AccessLog accessLog = new AccessLog().setErrMsg(entity.getRemark())
                .setIp(entity.getIp()).setUrl(entity.getUrl())
                .setMillis(entity.getExecTime()).setStatus(entity.isStatus());

        if(ShiroUtil.isLogin()) {
            final Admin admin = ShiroUtil.getUser();
            accessLog.setUserId(admin.getId()).setUserName(admin.getName());
        }

        final Menu menu = getLoggerMenu(entity.getUrl());
        if(menu != null) {
            accessLog.setTitle(menu.getName());
            final Long pid = menu.getPid();

            // 获取所属模块
            if(pid != null && pid > 0) {
                final Menu parent = this.menuService.getByIdFromCache(pid);
                if(parent != null) {
                    accessLog.setMsn(parent.getName());
                }
            }
        }

        operaLogService.save(accessLog);
    }

    @Override
    public Menu getLoggerMenu(String url) {
        return menuService.getByUrlFromCache(url);
    }

}
