package com.iteaj.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * create time: 2020/6/25
 *  资源管理
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping
public class ResourceController extends BaseController {

    private final ResourceManager resourceManager;

    public ResourceController(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * 返回环境相关配置
     * @return
     */
    @GetMapping("/env")
    public Result envConfig() {
        Map<String, Object> config =  resourceManager
                .getEnvConfig(SecurityUtils.getPrincipal());
        return success().add("env", config);
    }

    /**
     * 权限资源列表
     * @return
     */
    @GetMapping("/resources")
    public Result resources() {
        BaseEntity principal = SecurityUtils.getPrincipal();
        return success().add("resources", resourceManager.getResources(principal));
    }

    /**
     * 获取视图菜单下面的功能点
     * @param id 指定视图
     * @return
     */
    @GetMapping("/point")
    public Result getFunctionPoint(Long id) {
        return success().add("rows", resourceManager.getFunctionPoint(id));
    }
}
