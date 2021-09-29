package com.iteaj.msn.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.msn.core.dto.RoleDto;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.entity.Role;
import com.iteaj.msn.core.service.IMenuService;
import com.iteaj.msn.core.service.IRoleService;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * create time: 2019/11/27
 *  角色管理
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/role")
public class RoleController extends BaseController {

    private final IRoleService roleService;
    private final IMenuService menuService;

    public RoleController(IRoleService roleService, IMenuService menuService) {
        this.roleService = roleService;
        this.menuService = menuService;
    }

    @GetMapping("/view")
    @RequiresPermissions("core:role:view")
    public Result<IPage<Role>> view(Page page, Role role) {
        page.addOrder(OrderItem.asc("sort"));
        return this.roleService.page(page, role);
    }

    @GetMapping("/list")
    public Result<List<Role>> view(Role role) {
        return this.roleService.list(role);
    }

    /**
     * 所有功能菜单
     * @return
     */
    @GetMapping("allMenus")
    public Result<List<Menu>> allFunc() {
        return this.menuService.selectMenuTrees(null);
    }

    @PostMapping("/add")
    @RequiresPermissions("core:add:view")
    public Result add(@RequestBody RoleDto role) {
        this.roleService.createRoleAndPerms(role);
        return success();
    }

    /**
     * 获取详情记录
     * @param id
     * @return
     */
    @GetMapping("/edit")
    public Result<RoleDto> edit(Long id) {
        return this.roleService.detail(id);
    }

    /**
     * 返回此角色拥有的权限列表
     * @param id
     * @return
     */
    @GetMapping("/func")
    public Result<List<Long>> func(Long id) {
        RoleDto detail = this.roleService.detail(id).ofNullable()
                .orElse(new RoleDto().setMenuIds(Collections.EMPTY_LIST));

        return success(detail.getMenuIds());
    }

    @PostMapping("/edit")
    @RequiresPermissions("core:role:edit")
    public Result edit(@RequestBody Role role) {
        this.roleService.updateById(role);
        return success();
    }

    /**
     * @param role
     * @return
     */
    @PostMapping("/perm")
    @RequiresPermissions("core:role:perm")
    public Result<Boolean> editFunc(@RequestBody RoleDto role) {
        this.roleService.updateRolePermsById(role);
        return success();
    }

    @PostMapping("/del")
    @RequiresPermissions("core:del:view")
    public Result del(@RequestBody List<Long> list) {
        this.roleService.deleteAllJoinByIds(list);
        return success();
    }
}
