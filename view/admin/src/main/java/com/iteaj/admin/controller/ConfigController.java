package com.iteaj.admin.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.admin.dto.RoleDto;
import com.iteaj.admin.entity.Config;
import com.iteaj.admin.entity.Role;
import com.iteaj.admin.service.IConfigService;
import com.iteaj.admin.service.IRoleService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private IConfigService configService;

    @GetMapping
    @RequiresPermissions(value = {"core:config:view", "core:config:test"}, logical = Logical.AND)
    public String view() {
        return "config";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:config:view")
    public Result view(Page page, Config config) {
        return this.configService.selectByPage(page, config).response(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:config:add")
    public Result add(@RequestBody Config config) {
        return this.configService.insert(config).response(success());
    }

    @ResponseBody
    @GetMapping("/edit")
    public Result edit(Long id) {
        return this.configService.getById(id).response(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:config:edit")
    public Result edit(@RequestBody Config config) {
        return this.configService.updateById(config).response(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:config:del")
    public Result del(@RequestBody List<Long> list) {
        return this.configService.deleteByIds(list).response(success());
    }
}
