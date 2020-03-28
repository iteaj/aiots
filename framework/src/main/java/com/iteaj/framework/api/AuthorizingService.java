package com.iteaj.framework.api;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.Resource;
import com.iteaj.framework.UrlResource;
import com.iteaj.framework.authority.AuthException;
import com.iteaj.framework.authority.AuthToken;
import com.iteaj.framework.authority.ModuleResource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 后台权限认证服务<br>
 *      注：后台管理项目启动的时候必须提供这个服务的具体实现, 否则将出错
 * @param <T>
 */
public interface AuthorizingService<T extends AuthToken> {

    /**
     * 获取账号信息
     * @param token
     * @return
     */
    BaseEntity getAdmin(T token) throws AuthException;

    /**
     * 密码校验
     * @param admin
     * @return
     */
    boolean validCredentials(BaseEntity admin, T token);

    /**
     * 获取指定管理员下面的指定Url权限资源列表
     * @param admin
     * @param msn
     * @return
     */
    ModuleResource getModuleResource(BaseEntity admin, String msn);

    /**
     * 返回指定用戶的权限资源
     * @return
     */
    List<Resource> getResources(BaseEntity principal);

    /**
     * 返回视图菜单下面的功能点
     * @param id
     * @return
     */
    List<UrlResource> getFunctionPoint(Serializable id);

    /**
     * 获取管理员拥有的角色列表
     * @param admin
     * @return
     */
    Collection<String> getRoles(BaseEntity admin);

    /**
     * 获取管理员拥有的权限列表
     * @param admin
     * @return
     */
    Collection<String> getPermissions(BaseEntity admin);

    /**
     * 返回环境相关配置
     * @return
     */
    Map<String, Object> getEnvConfig(BaseEntity admin);
}
