package com.iteaj.develop.controller;

import java.util.List;
import com.iteaj.util.Result;
import com.iteaj.develop.entity.GenOnline;
import com.iteaj.develop.service.IGenOnlineService;
import com.iteaj.util.module.mvc.result.ListResult;
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
@RequestMapping("/dev/online")
public class GenOnlineController extends BaseController {

    @Autowired
    private IGenOnlineService genOnlineService;

    @GetMapping
    @RequiresPermissions("dev:online:view")
    public String view() {
        return "genOnline";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("dev:online:view")
    public Result<IPage<GenOnline>> view(GenOnline entity, Page page) {
        return this.genOnlineService.selectByPage(page, entity);
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("dev:online:add")
    public Result<Boolean> add(@RequestBody GenOnline entity) {
        return this.genOnlineService.insert(entity).build(success("新增记录成功"));
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("dev:online:edit")
    public Result<Boolean> edit(@RequestBody GenOnline entity) {
        return this.genOnlineService.updateById(entity).build(success("修改记录成功"));
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("dev:online:detail")
    public Result<GenOnline> detail(Long id) {
        return this.genOnlineService.getById(id);
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("dev:online:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.genOnlineService.deleteByIds(ids).build(success("删除记录成功"));
    }

    @ResponseBody
    @GetMapping("/foreign")
    public Result foreign() {
        return this.genOnlineService.selectList((GenOnline) null);
    }

    @ResponseBody
    @PostMapping("/gen")
    @RequiresPermissions("dev:online:gen")
    public Result<Boolean> gen(@RequestBody List<Long> onlines) {
        this.genOnlineService.selectByIds(onlines).forEach(item -> {

        });
        return success();
    }
}

