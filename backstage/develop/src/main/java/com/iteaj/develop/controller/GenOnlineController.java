package com.iteaj.develop.controller;

import java.util.List;
import com.iteaj.util.Result;
import com.iteaj.develop.entity.GenOnline;
import com.iteaj.develop.service.IGenOnlineService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import com.iteaj.framework.BaseController;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-09-13
 */
@Controller
@RequestMapping("/develop/genOnline")
public class GenOnlineController extends BaseController {

    @Autowired
    private IGenOnlineService genOnlineService;

    @GetMapping
    @RequiresPermissions("core:genOnline:view")
    public String view() {
        return "genOnline";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:genOnline:view")
    public Result<IPage<GenOnline>> view(GenOnline entity, Page page) {
        return this.genOnlineService.selectByPage(page, entity);
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:genOnline:add")
    public Result<Boolean> add(@RequestBody GenOnline entity) {
        return this.genOnlineService.insert(entity).build(success("新增记录成功"));
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:genOnline:edit")
    public Result<Boolean> edit(@RequestBody GenOnline entity) {
        return this.genOnlineService.updateById(entity).build(success("修改记录成功"));
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:genOnline:detail")
    public Result<GenOnline> detail(Long id) {
        return this.genOnlineService.getById(id);
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:genOnline:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.genOnlineService.deleteByIds(ids).build(success("删除记录成功"));
    }

}

