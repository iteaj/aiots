package com.iteaj.framework.spi.admin;

import com.iteaj.framework.Entity;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
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
     * 获取数据通过url信息
     * @param url
     * @return
     */
    UrlResource getByUrl(String url);

    /**
     * 返回字典列表
     * @return
     */
    List<DictResource> getDictResources();

    /**
     * 批量新增菜单
     * @param resources
     */
    void batchSave(List<MenuResource> resources);

    /**
     * 返回指定用戶的权限资源
     * @return
     */
    List<MenuResource> getResources(Entity principal);

    /**
     * 返回菜单类型
     * @param types {@code MenuType}
     * @return
     */
    Collection<MenuResource> listResourcesByType(MenuType... types);

    /**
     * 返回环境相关配置
     * @return
     */
    Map<String, Object> getEnvConfig(Entity admin);
}
