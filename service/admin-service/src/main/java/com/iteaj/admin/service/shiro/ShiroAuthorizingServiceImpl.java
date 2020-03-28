package com.iteaj.admin.service.shiro;

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
import com.iteaj.framework.Resource;
import com.iteaj.framework.UrlResource;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.authority.AuthException;
import com.iteaj.framework.authority.AuthToken;
import com.iteaj.framework.authority.ModuleResource;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.util.DigestUtils;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.ListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SofaService(interfaceType = AuthorizingService.class, bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
public class ShiroAuthorizingServiceImpl implements AuthorizingService<AuthToken>, EnvironmentAware {

    private final IMenuService menuService;
    private final IAdminService adminService;
    private final IConfigService configService;

    private Environment environment;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ShiroAuthorizingServiceImpl(IMenuService menuService
            , IAdminService adminService, IConfigService configService) {
        this.menuService = menuService;
        this.adminService = adminService;
        this.configService = configService;
    }

    @Override
    public BaseEntity getAdmin(AuthToken token) {
        if(null == token) throw new AuthException("错误的token");

        long start = System.currentTimeMillis();
        Admin byAccount = adminService.getByAccount((String) token.getPrincipal());
        System.out.println(System.currentTimeMillis() - start);
        return byAccount;
    }

    @Override
    public boolean validCredentials(BaseEntity admin, AuthToken token) {
        if(admin instanceof Admin) {
            char[] tokenPassword = ((String) token.getCredentials()).toCharArray();
            String password = ((Admin) admin).getPassword().toUpperCase();

            String toString = String.valueOf(tokenPassword);
            String md5Hex = DigestUtils.md5Hex(toString.getBytes()).toUpperCase();

            return Objects.equals(password, md5Hex);
        } else {
            logger.warn("type：权限服务 - action：密码校验 - status：fail - message：未知的管理员实体对象, 期待[{}]"
                    , Admin.class.getSimpleName());
            return false;
        }
    }

    @Override
    public ModuleResource getModuleResource(BaseEntity admin, String msn) {
        return null;
    }

    @Override
    public List<Resource> getResources(BaseEntity principal) {
        if(null == principal) throw new ServiceException("用户未登录");
        ListResult<Menu> menuTrees = menuService
                .selectMenuBarTrees(principal.getId()
                        , ShiroUtils.isSuper(principal.getId()));

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
    public Collection<String> getRoles(BaseEntity admin) {
        return null;
    }

    @Override
    public Collection<String> getPermissions(BaseEntity admin) {
        return null;
    }

    @Override
    public Map<String, Object> getEnvConfig(BaseEntity entity) {
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
