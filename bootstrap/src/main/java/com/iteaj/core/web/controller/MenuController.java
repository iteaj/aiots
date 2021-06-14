package com.iteaj.core.web.controller;

import com.iteaj.core.entity.Menu;
import com.iteaj.core.service.IMenuService;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @RequiresPermissions("core:menu:view")
    public String view() {
        return "menu";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:menu:view")
    public Result list(Menu menu) {
        return menuService.selectMenuTrees(menu).build(success());
    }

    @ResponseBody
    @GetMapping("/parent")
    public Result parentList() {
        // 不包含菜单类型是权限的菜单
        return menuService.selectParentTrees();
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:menu:add")
    public Result add(@RequestBody Menu menu) {
        return menuService.insert(menu);
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:menu:del")
    public Result del(@RequestBody List<Long> menus) {
        return menuService.deleteByIds(menus);
    }

    @GetMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result edit(Integer id) {
        return menuService.getById(id);
    }

    @PostMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result edit(@RequestBody Menu menu) {
        return menuService.updateById(menu);
    }

    /**
     * 更新排序
     * @param menu
     * @return
     */
    @ResponseBody
    @PostMapping("/sort")
    public Result sort(@RequestBody Menu menu, String type) {
//        this.menuService.updateMenuSort(menu);
        return success();
    }
}
