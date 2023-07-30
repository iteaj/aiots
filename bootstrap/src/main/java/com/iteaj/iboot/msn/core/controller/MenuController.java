package com.iteaj.iboot.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.iboot.msn.core.entity.Menu;
import com.iteaj.iboot.msn.core.service.IMenuService;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统菜单管理
 * @author iteaj
 */
@RestController
@RequestMapping("/core/menu")
public class MenuController extends BaseController {

    private final List<Module> modules;
    private final IMenuService menuService;

    public MenuController(List<Module> modules, IMenuService menuService) {
        this.modules = modules;
        this.menuService = menuService;
    }

    /**
     * 获取菜单列表
     * @param menu
     * @return
     */
    @Logger("查询菜单列表")
    @GetMapping("/view")
    @CheckPermission("core:menu:view")
    public Result<List<Menu>> list(Menu menu) {
        return menuService.selectMenuTrees(menu);
    }

    /**
     * 菜单栏菜单列表
     * @return 菜单列表
     */
    @GetMapping("/bars")
    public Result<List<Menu>> bars() {
        Long loginId = SecurityUtil.getLoginId().map(item -> (Long) item).orElse(null);
        return menuService.selectMenuBarTrees(loginId, SecurityUtil.isSuper());
    }

    /**
     * 获取类型是目录和视图的菜单列表
     * @return
     */
    @GetMapping("/parent")
    public Result<List<Menu>> parentList() {
        // 不包含菜单类型是权限的菜单
        return menuService.selectParentTrees();
    }

    /**
     * 新增菜单
     * @param menu
     * @return
     */
    @Logger("新增菜单记录")
    @PostMapping("/add")
    @CheckPermission("core:menu:add")
    public Result<Boolean> add(@RequestBody Menu menu) {
        return menuService.save(menu);
    }

    /**
     * 删除菜单
     * @param idList
     * @return
     */
    @Logger("删除菜单记录")
    @PostMapping("/del")
    @CheckPermission("core:menu:del")
    public Result<Boolean> del(@RequestBody List<Long> idList) {
        return menuService.removeByIds(idList);
    }

    /**
     * 获取编辑详情
     * @param id
     * @return
     */
    @GetMapping("/edit")
    @CheckPermission("core:menu:edit")
    public Result<Menu> edit(Long id) {
        return menuService.getById(id);
    }

    /**
     * 编辑菜单
     * @param menu
     * @return
     */
    @Logger("修改菜单记录")
    @PostMapping("/edit")
    @CheckPermission("core:menu:edit")
    public Result<Boolean> edit(@RequestBody Menu menu) {
        return menuService.updateById(menu);
    }

    /**
     * 获取模块列表
     * @return
     */
    @GetMapping("msn")
    public Result<List<Module>> msn() {
        return success(this.modules);
    }
}
