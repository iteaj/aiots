package com.iteaj.framework.api;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.Resource;
import com.iteaj.framework.UrlResource;
import com.iteaj.framework.authority.ModuleResource;
import com.iteaj.framework.authority.Result;
import com.iteaj.framework.authority.TreeResource;
import com.iteaj.util.module.mvc.orm.Entity;

import java.io.Serializable;
import java.util.Collection;

/**
 * 授权资源管理
 */
public interface AuthorityManager {

    /**
     * 注册应用权限
     * @param moduleResource 应用的权限列表
     * @return
     */
    Result register(ModuleResource moduleResource);

    /**
     * 获取所有权限资源
     * @return
     */
    Collection<? extends Resource> getResources(BaseEntity principal);

    /**
     *  获取指定视图菜单下面的功能点
     * @return
     */
    Collection<? extends Resource> getFunctionPoint(Serializable id);

    /**
     * 是否存在Url菜单
     * @param urlAuthority
     * @return
     */
    boolean exists(String urlAuthority);

    /**
     * 判断指定应用是否存在 Url菜单
     * @param appId
     * @param urlAuthority
     * @return
     */
    boolean exists(String appId, String urlAuthority);

    /**
     * 往url权限新增功能点
     * @param parent 树权限
     * @param urlResource url权限
     * @return
     */
    Result add(Entity admin, TreeResource parent, UrlResource urlResource);

    /**
     * 往url权限新增功能点列表
     * @param parent
     * @param resources
     * @return
     */
    Result add(Entity admin, TreeResource parent, Collection<UrlResource> resources);

    /**
     * 移除url权限下面的指定功能点
     * @param urlResource
     * @return
     */
    Result remove(UrlResource urlResource);

    /**
     * 替换指定url权限下面的功能点
     * @param ori 被替换的功能点
     * @param target 要替换的功能点
     * @return
     */
    Result replace(UrlResource ori, UrlResource target);
}
