package com.iteaj.msn.core.controller;

import com.iteaj.msn.core.entity.Menu;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.Entity;
import com.iteaj.framework.result.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *  系统主页功能
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core")
public class MainController extends BaseController {

    @Autowired
    private ResourceManager resourceManager;

    /**
     * 返回环境信息
     * @return
     */
    @GetMapping("/env")
    public Result envConfig() {
        Map<String, Object> config =  resourceManager
                .getEnvConfig(ShiroUtil.getUser());
        return success(config);
    }

    /**
     * 获取模块列表
     * @return
     */
    @GetMapping("/msn")
    public Result<List<Module>> modules() {
        List<Module> modules = resourceManager.getModules();
        return new ListResult(modules);
    }

    /**
     * 获取菜单栏数据
     * @return
     */
    @GetMapping("/menus")
    public Result<List<Menu>> resources() {
        final Entity user = ShiroUtil.getUser();
        resourceManager.getResources(user).forEach(item -> {

        });
        return new ListResult(resourceManager.getResources(user));
    }

}
