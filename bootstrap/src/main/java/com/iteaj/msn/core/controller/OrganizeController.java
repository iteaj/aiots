package com.iteaj.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.msn.core.entity.Organize;
import com.iteaj.msn.core.service.IOrganizeService;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织部门管理
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/org")
public class OrganizeController extends BaseController {

    private final IOrganizeService organizeService;

    public OrganizeController(IOrganizeService organizeService) {
        this.organizeService = organizeService;
    }

    /**
     * 获取部门列表
     * @param organize
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:org:view")
    public Result<List<Organize>> view(Organize organize) {
        return this.organizeService.selectTrees(organize);
    }

    @ResponseBody
    @GetMapping("/parent")
    public Result parent() {
        return this.organizeService.selectTrees(null);
    }

    /**
     * 新增部门
     * @param organize
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions("core:org:add")
    public Result add(@RequestBody Organize organize) {
        return this.organizeService.save(organize);
    }

    /**
     * 获取编辑详情
     * @param id
     * @return
     */
    @GetMapping("/edit")
    @RequiresPermissions("core:org:edit")
    public Result edit(Integer id) {
        return this.organizeService.getById(id);
    }

    /**
     * 修改部门
     * @param organize
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions("core:org:edit")
    public Result edit(@RequestBody Organize organize) {
        return this.organizeService.updateById(organize);
    }

    /**
     * 删除部门
     * @param list
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions("core:org:del")
    public Result add(@RequestBody List<Long> list) {
        return this.organizeService.removeByIds(list);
    }
}
