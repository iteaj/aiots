package com.iteaj.modules.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.modules.core.dto.RoleDto;
import com.iteaj.modules.core.entity.Role;
import com.iteaj.modules.core.service.IRoleService;
import com.iteaj.util.Result;
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
@RequestMapping("/core/role")
public class RoleController extends BaseController {

    private final IRoleService roleService;

    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String view() {
        return "role";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:role:view")
    public Result view(Page page, Role role) {
        return this.roleService.selectByPage(page, role).build(success());
    }

    @ResponseBody
    @GetMapping("/list")
    public Result view(Role role) {
        return this.roleService.selectList(role).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:add:view")
    public Result add(@RequestBody RoleDto role) {
        this.roleService.createRoleAndPerms(role);
        return success();
    }

    @ResponseBody
    @GetMapping("/edit")
    public Result edit(Long id) {
        return this.roleService.detail(id).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:edit:view")
    public Result edit(@RequestBody RoleDto role) {
        this.roleService.updateRoleAndPermsById(role);
        return success();
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:del:view")
    public Result del(@RequestBody List<Long> list) {
        this.roleService.deleteAllJoinByIds(list);
        return success();
    }
}
