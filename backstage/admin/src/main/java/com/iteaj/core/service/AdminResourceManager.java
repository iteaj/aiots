package com.iteaj.core.service;

import com.iteaj.core.entity.Admin;
import com.iteaj.core.entity.Config;
import com.iteaj.core.entity.Menu;
import com.iteaj.core.enums.ConfigType;
import com.iteaj.framework.spi.admin.*;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.orm.Entity;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create time: 2020/6/26
 *  后台系统资源管理
 * @author iteaj
 * @since 1.0
 */
@Component
public class AdminResourceManager implements ResourceManager, EnvironmentAware {

    private Environment environment;
    private final List<Module> modules;
    private final IMenuService menuService;
    private final IConfigService configService;

    public AdminResourceManager(IMenuService menuService
            , IConfigService configService, List<Module> modules) {
        this.modules = modules;
        this.menuService = menuService;
        this.configService = configService;
    }

    @Override
    public List<MenuResource> getResources(Entity principal) {
        if(null == principal) throw new ServiceException("用户未登录");

        ListResult<Menu> menuTrees = menuService
                .selectMenuBarTrees((Long) principal.getId()
                        , SecurityUtils.isSuper((Long) principal.getId()));

        return menuTrees.get().stream().collect(Collectors.toList());
    }

    @Override
    public List<UrlResource> getFunctionPoint(Serializable id) {
        Menu menu = new Menu();
        menu.setPid((Long) id);
        return menuService.selectList(menu)
                .get().stream().collect(Collectors.toList());
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public Map<String, Object> getEnvConfig(Entity entity) {
        Map<String, Object> config = new HashMap<>();
        // 当前登录用户
        Admin admin = (Admin)entity;
        Map<String, Object> user = new HashMap<>();
        user.put("id", admin.getId());
        user.put("sex", admin.getSex());
        user.put("name", admin.getName());
        user.put("email", admin.getEmail());
        user.put("phone", admin.getPhone());
        user.put("orgId", admin.getOrgId());
        user.put("remark", admin.getRemark());
        user.put("avatar", admin.getAvatar());
        user.put("account", admin.getAccount());
        config.put("user", user);

        // 系统当前环境
        String[] profiles = this.environment.getActiveProfiles();
        config.put("profiles", profiles);

        // 系统配置
        Map<String, Config> configMap = this.configService.selectList(new Config(ConfigType.sys))
                .get().stream().collect(Collectors.toMap(key -> key.getLabel(), val -> val));
        config.put("config", configMap);
        return config;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
