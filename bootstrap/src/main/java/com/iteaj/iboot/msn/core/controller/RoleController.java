package com.iteaj.iboot.msn.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.iboot.msn.core.dto.RoleDto;
import com.iteaj.iboot.msn.core.dto.RoleFuncDto;
import com.iteaj.iboot.msn.core.entity.Menu;
import com.iteaj.iboot.msn.core.entity.Role;
import com.iteaj.iboot.msn.core.service.IMenuService;
import com.iteaj.iboot.msn.core.service.IRoleService;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
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

    /**
     * 角色列表
     * @param page
     * @param role
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:role:view")
    public Result<IPage<Role>> view(Page page, Role role) {
        page.addOrder(OrderItem.asc("sort"));
        return this.roleService.page(page, role);
    }

    /**
     * 获取所有角色列表
     * @param role
     * @return
     */
    @GetMapping("/list")
    public Result<List<Role>> list(Role role) {
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

    /**
     * 新增角色记录
     * @param role
     * @return
     */
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
    public Result<Role> edit(Long id) {
        return this.roleService.getById(id);
    }

    /**
     * 保存编辑记录
     * @param role
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions("core:role:edit")
    public Result<Boolean> edit(@RequestBody Role role) {
        return this.roleService.updateById(role);
    }

    /**
     * 返回此角色拥有的权限列表
     * @param id
     * @return
     */
    @GetMapping("/func")
    public Result<RoleFuncDto> func(Long id) {
        RoleFuncDto roleFuncDto = new RoleFuncDto(id, this.roleService.listMenusOfRole(id).getData());
        return success(roleFuncDto);
    }

    /**
     * 修改角色权限
     * @param role
     * @return
     */
    @PostMapping("/perm")
    @RequiresPermissions("core:role:perm")
    public Result<Boolean> editFunc(@RequestBody RoleDto role) {
        this.roleService.updateRolePermsById(role);
        return success();
    }

    /**
     * 删除记录
     * @param list
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions("core:del:view")
    public Result del(@RequestBody List<Long> list) {
        this.roleService.delRoleAndPermByIds(list);
        return success();
    }
}
