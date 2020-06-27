package com.iteaj.framework.spi.admin.auth;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.MenuResource;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.admin.UrlResource;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.orm.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 后台权限认证服务<br>
 *      注：后台管理项目启动的时候必须提供这个服务的具体实现, 否则将出错
 */
public interface AuthorizingService{

    /**
     * 获取管理员拥有的角色列表
     * @param admin
     * @return
     */
    Collection<String> getRoles(Entity admin);

    /**
     * 获取管理员拥有的权限列表
     * @param admin
     * @return
     */
    Collection<String> getPermissions(Entity admin);

}
