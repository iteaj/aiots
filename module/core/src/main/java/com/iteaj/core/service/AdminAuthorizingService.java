package com.iteaj.core.service;

import com.iteaj.core.entity.Admin;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.admin.auth.AuthorizingService;
import com.iteaj.framework.spi.UserAccountService;
import com.iteaj.util.DigestUtils;
import com.iteaj.util.module.mvc.orm.Entity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 后台管理端的认证和权限服务
 * @see AuthorizingService
 */
public class AdminAuthorizingService implements AuthorizingService, UserAccountService<AuthToken> {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IAdminService adminService;


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
    public BaseEntity getUser(AuthToken token) {
        return adminService.getByAccount((String) token.getPrincipal());
    }

    @Override
    public boolean validCredentials(Entity admin, AuthToken token) {
        if(admin instanceof Admin) {
            String password = ((Admin) admin).getPassword().toUpperCase();

            String toString = "";
            if(token.getCredentials() instanceof char[]) {
                toString = new String((char[]) token.getCredentials());
            } else {
                toString = (String) token.getCredentials();
            }
            String md5Hex = DigestUtils.md5Hex(toString.getBytes()).toUpperCase();

            return Objects.equals(password, md5Hex);
        } else {
            return false;
        }
    }
}
