package com.iteaj.admin.controller;

import com.iteaj.admin.entity.Organize;
import com.iteaj.admin.service.IOrganizeService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/26
 *  组织机构管理
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/core/org")
public class OrganizeController extends BaseController {

    private final IOrganizeService organizeService;

    public OrganizeController(IOrganizeService organizeService) {
        this.organizeService = organizeService;
    }

    @GetMapping
    @RequiresPermissions("core:org:view")
    public String view() {
        return "organize";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:org:view")
    public Result view(Organize organize) {
        return this.organizeService.selectTrees(organize).response(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:org:add")
    public Result add(@RequestBody Organize organize) {
        return this.organizeService.insert(organize).response(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:org:edit")
    public Result edit(@RequestBody Organize organize) {
        return this.organizeService.updateById(organize).response(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:org:del")
    public Result add(List<Long> list) {
        return this.organizeService.deleteByIds(list).response(success());
    }
}
