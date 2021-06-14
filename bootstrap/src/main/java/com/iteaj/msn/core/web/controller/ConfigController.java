package com.iteaj.msn.core.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.msn.core.entity.Config;
import com.iteaj.msn.core.service.IConfigService;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/core/config")
public class ConfigController extends BaseController {

    private final IConfigService configService;

    public ConfigController(IConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    @RequiresPermissions(value = {"core:config:view", "core:config:test"}, logical = Logical.AND)
    public String view() {
        return "config";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:config:view")
    public Result view(Page page, Config config) {
        return this.configService.selectByPage(page, config).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:config:add")
    public Result<Boolean> add(@RequestBody Config config) {
        return this.configService.insert(config).build(success());
    }

    @ResponseBody
    @GetMapping("/edit")
    public Result<Config> edit(Long id) {
        return this.configService.getById(id).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:config:edit")
    public Result<Boolean> edit(@RequestBody Config config) {
        return this.configService.updateById(config).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:config:del")
    public Result<Boolean> del(@RequestBody List<Long> list) {
        return this.configService.deleteByIds(list).build(success());
    }
}
