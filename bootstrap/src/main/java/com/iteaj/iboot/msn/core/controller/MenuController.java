package com.iteaj.iboot.msn.core.controller;

import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.iboot.msn.core.entity.Menu;
import com.iteaj.iboot.msn.core.service.IMenuService;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统菜单管理
 * @author iteaj
 */
@RestController
@RequestMapping("/core/menu")
public class MenuController extends BaseController {

    private final IMenuService menuService;

    public MenuController(IMenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 获取菜单列表
     * @param menu
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:menu:view")
    public Result<List<Menu>> list(Menu menu) {
        return menuService.selectMenuTrees(menu);
    }

    /**
     * @return 菜单栏所需的列表
     */
    @GetMapping("/bars")
    public Result<List<Menu>> bars() {
        return menuService.selectMenuBarTrees((Long) ShiroUtil.getId(), ShiroUtil.isSuper());
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
    @PostMapping("/add")
    @RequiresPermissions("core:menu:add")
    public Result<Boolean> add(@RequestBody Menu menu) {
        return menuService.save(menu);
    }

    /**
     * 删除菜单
     * @param idList
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions("core:menu:del")
    public Result<Boolean> del(@RequestBody List<Long> idList) {
        return menuService.removeByIds(idList);
    }

    /**
     * 获取编辑详情
     * @param id
     * @return
     */
    @GetMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result<Menu> edit(Long id) {
        return menuService.getById(id);
    }

    /**
     * 编辑菜单
     * @param menu
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result<Boolean> edit(@RequestBody Menu menu) {
        return menuService.updateById(menu);
    }

}
