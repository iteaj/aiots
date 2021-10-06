package com.iteaj.msn.core.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.spi.admin.*;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.entity.Config;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.enums.ConfigType;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.Entity;
import com.iteaj.framework.result.ListResult;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * create time: 2020/6/26
 *  后台系统资源管理
 * @author iteaj
 * @since 1.0
 */
public class MenuResourceManager implements ResourceManager, EnvironmentAware {

    private Environment environment;
    private final List<Module> modules;
    private final IMenuService menuService;
    private final IConfigService configService;

    public MenuResourceManager(IMenuService menuService
            , IConfigService configService, List<Module> modules) {
        this.menuService = menuService;
        this.modules = modules;
        this.configService = configService;
    }

    @Override
    public UrlResource getByUrl(String url) {
        return menuService.getByUrl(url);
    }

    @Override
    public void batchSave(List<MenuResource> resources) {

    }

    @Override
    public List<MenuResource> getResources(Entity principal) {
        if(null == principal) throw new ServiceException("用户未登录");

        ListResult<Menu> menuTrees = menuService.selectMenuBarTreesInModules
                ((Long) principal.getId(), getModules(), ShiroUtil.isSuper());

        return menuTrees.stream().collect(Collectors.toList());
    }

    @Override
    public Collection<MenuResource> listResourcesByType(MenuType... types) {
        ListResult<Menu> listResult = menuService.list(Wrappers
                .<Menu>lambdaQuery().in(Menu::getType, types));

        Collection<Menu> menus = TreeUtils.toTrees(listResult.getData(), 0l);

        return menus.stream().map(item -> (MenuResource)item)
                .collect(Collectors.toCollection(ArrayList::new));
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
        Map<String, Config> configMap = this.configService.list(new Config(ConfigType.sys))
                .stream().collect(Collectors.toMap(key -> key.getLabel(), val -> val));
        config.put("config", configMap);
        return config;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
