package com.iteaj.modules.core.controller;

import com.iteaj.core.entity.Menu;
import com.iteaj.core.service.IMenuService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * jdk：1.8
 *  系统菜单管理
 * @author iteaj
 * create time：2019/5/19
 */
@Controller
@RequestMapping("/core/menu")
public class MenuController extends BaseController {

    private final IMenuService menuService;

    public MenuController(IMenuService menuService) {
        this.menuService = menuService;
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
    public Result parentList(Menu menu) {
        // 不包含菜单类型是权限的菜单
        return menuService.selectTreesForNoA(menu).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:menu:add")
    public Result add(@RequestBody Menu menu) {
        return menuService.insert(menu).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:menu:del")
    public Result del(@RequestBody List<Long> menus) {
        return menuService.deleteByIds(menus).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:menu:edit")
    public Result edit(@RequestBody Menu menu) {
        return menuService.updateById(menu).build(success());
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

    /**
     * 创建指定视图下面的默认功能点, 增、删、改、查
     * @param menu
     * @return
     */
    @ResponseBody
    @PostMapping("/auth")
    public Result auth(@RequestBody Menu menu) {
        this.menuService.createDefaultAuth(menu);
        return success();
    }
}
