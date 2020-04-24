package com.iteaj.admin.service;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.iteaj.admin.entity.Admin;
import com.iteaj.admin.entity.Config;
import com.iteaj.admin.entity.Menu;
import com.iteaj.admin.enums.ConfigType;
import com.iteaj.admin.service.IAdminService;
import com.iteaj.admin.service.IConfigService;
import com.iteaj.admin.service.IMenuService;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.authority.AuthException;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.izone.spi.AuthorizingService;
import com.iteaj.izone.spi.AuthzUrl;
import com.iteaj.izone.spi.Resource;
import com.iteaj.izone.spi.UrlResource;
import com.iteaj.izone.spi.event.EventPublisher;
import com.iteaj.izone.web.shiro.AbstractShiroAuthorService;
import com.iteaj.izone.web.shiro.ShiroAuthToken;
import com.iteaj.util.DigestUtils;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.HttpResult;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.orm.Entity;
import com.iteaj.util.module.mvc.result.ListResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 由Shiro框架认证的用户名和密码的权限管理服务
 * @see AuthorizingService
 */
@Service
@SofaService(interfaceType = AuthorizingService.class
        , bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
public class AdminShiroAuthorizingService extends
        AbstractShiroAuthorService implements EnvironmentAware {

    private final AuthzUrl authzUrl;
    private final IRoleService roleService;
    private final IMenuService menuService;
    private final IAdminService adminService;
    private final IConfigService configService;

    private Environment environment;

    public AdminShiroAuthorizingService(IRoleService roleService, IMenuService menuService
            , IAdminService adminService, IConfigService configService, AuthzUrl authzUrl) {
        this.roleService = roleService;
        this.menuService = menuService;
        this.adminService = adminService;
        this.configService = configService;
        this.authzUrl = authzUrl;
    }

    @Override
    public AuthzUrl getAuthzUrl() {
        return this.authzUrl;
    }

    @Override
    public BaseEntity getAdmin(ShiroAuthToken token) {
        if(null == token) throw new AuthException("错误的参数token");
        return adminService.getByAccount(token.getPrincipal());
    }

    @Override
    public boolean validCredentials(Entity admin, ShiroAuthToken token) {
        if(admin instanceof Admin) {
            String password = ((Admin) admin).getPassword().toUpperCase();

            String toString = String.valueOf(token.getPassword());
            String md5Hex = DigestUtils.md5Hex(toString.getBytes()).toUpperCase();

            return Objects.equals(password, md5Hex);
        } else {
            logger.warn("type：权限服务 - action：密码校验 - status：fail - message：未知的管理员实体对象, 期待[{}]"
                    , Admin.class.getSimpleName());
            return false;
        }
    }

    @Override
    public List<Resource> getResources(Entity principal) {
        if(null == principal) throw new ServiceException("用户未登录");
        ListResult<Menu> menuTrees = menuService
                .selectMenuBarTrees((Long) principal.getId()
                        , ShiroUtils.isSuper((Long) principal.getId()));

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
    public Collection<String> getRoles(Entity admin) {
        List<Long> roleIds = this.roleService.selectByAdminId(admin.getId());
        return roleIds.stream().map(id->String.valueOf(id)).collect(Collectors.toList());
    }

    @Override
    public Collection<String> getPermissions(Entity admin) {
        return this.adminService.selectPermsById(admin.getId());
    }

    @Override
    public Map<String, Object> getEnvConfig(Entity entity) {
        Map<String, Object> config = new HashMap<>();
        // 当前登录用户
        Admin admin = (Admin)entity;
        Map<String, Object> user = new HashMap<>();
        user.put("id", admin.getId());
        user.put("name", admin.getName());
        user.put("orgId", admin.getOrgId());
        user.put("status", admin.getStatus());
        user.put("avatar", admin.getAvatar());
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
