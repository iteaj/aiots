package com.iteaj.core.web.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.admin.*;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.orm.Entity;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
                .getEnvConfig(SecurityUtils.getUser());
        return success(config);
    }

    /**
     * 返回当前系统已经注册过的模块列表
     * @return
     */
    @GetMapping("/modules")
    public Result modules() {
        List<Module> modules = resourceManager.getModules();
        return new ListResult(modules).build(success());
    }
}
