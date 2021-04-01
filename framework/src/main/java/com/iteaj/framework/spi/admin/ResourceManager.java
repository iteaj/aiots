package com.iteaj.framework.spi.admin;

import com.iteaj.util.module.mvc.orm.Entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public interface ResourceManager {

    /**
     * 返回指定用戶的权限资源
     * @return
     */
    List<MenuResource> getResources(Entity principal);


    /**
     * 返回环境相关配置
     * @return
     */
    Map<String, Object> getEnvConfig(Entity admin);

    /**
     * 获取模块列表
     * @return
     */
    List<Module> getModules();
}
