package com.iteaj.msn.core.controller;

import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.service.IMenuService;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * jdk：1.8
 *  系统菜单管理
 * @author iteaj
 * create time：2019/5/19
 */
@RestController
@RequestMapping("/core/menu")
public class MenuController extends BaseController {

    private final IMenuService menuService;
    private final ResourceManager resourceManager;

    public MenuController(IMenuService menuService, ResourceManager resourceManager) {
        this.menuService = menuService;
        this.resourceManager = resourceManager;
    }

    @GetMapping("/view")
    @RequiresPermissions("core:menu:view")
    public Result<List<Menu>> list(Menu menu) {
        return menuService.selectMenuTreesInModules(menu, resourceManager.getModules());
    }

    @GetMapping("/parent")
    public Result<List<Menu>> parentList() {
        // 不包含菜单类型是权限的菜单
        return menuService.selectParentTrees();
    }

    @PostMapping("/add")
    @RequiresPermissions("core:menu:add")
    public Result<Boolean> add(@RequestBody Menu menu) {
        return menuService.save(menu);
    }

    @PostMapping("/del")
    @RequiresPermissions("core:menu:del")
    public Result<Boolean> del(@RequestBody List<Serializable> idList) {
        return menuService.removeByIds(idList);
    }

    @GetMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result<Menu> edit(Long id) {
        return menuService.getById(id);
    }

    @PostMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result<Boolean> edit(@RequestBody Menu menu) {
        return menuService.updateById(menu);
    }

    /**
     * 更新排序
     * @param menu
     * @return
     */
    @PostMapping("/sort")
    public Result sort(@RequestBody Menu menu, String type) {
//        this.menuService.updateMenuSort(menu);
        return success();
    }
}
