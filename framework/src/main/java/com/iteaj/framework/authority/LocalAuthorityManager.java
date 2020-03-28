package com.iteaj.framework.authority;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.Resource;
import com.iteaj.framework.UrlResource;
import com.iteaj.framework.api.AuthorityManager;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.util.module.mvc.orm.Entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理权限资源, 包括菜单和功能点
 */
public class LocalAuthorityManager implements AuthorityManager {

    private AuthorizingService authorizingService;
    private Map<String, UrlResource> menuMap = new ConcurrentHashMap<>(24);

    public LocalAuthorityManager(AuthorizingService authorizingService) {
        this.authorizingService = authorizingService;
    }

    @Override
    public Result register(ModuleResource moduleResource) {
        return null;
    }

    @Override
    public Collection getResources(BaseEntity principal) {
        return authorizingService.getResources(principal);
    }

    @Override
    public Collection<? extends Resource> getFunctionPoint(Serializable id) {
        return authorizingService.getFunctionPoint(id);
    }

    @Override
    public boolean exists(String urlAuthority) {
        return false;
    }

    @Override
    public boolean exists(String appId, String urlAuthority) {
        return false;
    }

    @Override
    public Result add(Entity admin, TreeResource parent, UrlResource urlResource) {
        return null;
    }

    @Override
    public Result add(Entity admin, TreeResource parent, Collection collection) {
        return null;
    }

    @Override
    public Result replace(UrlResource ori, UrlResource target) {
        return null;
    }

    @Override
    public Result remove(UrlResource urlResource) {
        return null;
    }
}
