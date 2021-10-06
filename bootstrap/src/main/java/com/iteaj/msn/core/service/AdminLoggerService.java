package com.iteaj.msn.core.service;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.logger.AccessLogger;
import com.iteaj.framework.logger.LoggerMenu;
import com.iteaj.framework.logger.LoggerService;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.spi.admin.*;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.entity.AccessLog;
import com.iteaj.framework.spi.admin.MenuType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create time: 2020/4/21
 *  管理端操作日志记录服务
 * @author iteaj
 * @since 1.0
 */
public class AdminLoggerService implements LoggerService, InitializingBean {

    @Autowired
    private IMenuService menuService;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private IAccessLogService operaLogService;

    private Map<Long, Menu> idMenuMap = new HashMap<>(128);
    private Map<String, Menu> urlMenuMap = new HashMap<>(128);

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

        final Menu menu = getMenu(entity.getUrl());
        if(menu != null) {
            accessLog.setTitle(menu.getName());
            final Long pid = menu.getPid();

            // 获取所属模块
            if(pid != null && pid > 0) {
                final Menu parent = this.idMenuMap.get(pid);
                if(parent != null) {
                    accessLog.setMsn(parent.getName());
                }
            }
        }

        operaLogService.save(accessLog);
    }

    @Override
    public LoggerMenu getLoggerMenu(String url) {
        return getMenu(url);
    }

    private Menu getMenu(String url) {
        return urlMenuMap.get(url);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Module> modules = resourceManager.getModules();

        // 获取已经注册的模块
        String[] msnList = modules.stream().map(item -> item.getMsn()).toArray(String[]::new);

        // 只查询已经注册过的模块的菜单列表
        this.menuService.list(Wrappers.<Menu>query().ne("type", MenuType.M)
                .in(ArrayUtils.isNotEmpty(msnList),"msn", msnList))
                .ofNullable().ifPresent(menus -> {
                menus.forEach(menu -> {
                    if(StringUtils.hasText(menu.getUrl())) {
                        urlMenuMap.put(menu.getUrl(), menu);
                    }

                    idMenuMap.put(menu.getId(), menu);
                });
        });

    }
}
