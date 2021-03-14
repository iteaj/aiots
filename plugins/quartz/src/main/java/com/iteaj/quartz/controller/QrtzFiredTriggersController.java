package com.iteaj.quartz.controller;

import java.util.List;

import com.iteaj.quartz.entity.QrtzFiredTriggers;
import com.iteaj.quartz.service.IQrtzFiredTriggersService;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import com.iteaj.framework.BaseController;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@Controller
@RequestMapping("/develop/qrtzFiredTriggers")
public class QrtzFiredTriggersController extends BaseController {

    @Autowired
    private IQrtzFiredTriggersService qrtzFiredTriggersService;

    @GetMapping
    @RequiresPermissions("core:qrtzFiredTriggers:view")
    public String view() {
        return "qrtzFiredTriggers";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:qrtzFiredTriggers:view")
    public Result view(QrtzFiredTriggers entity, Page page) {
        return this.qrtzFiredTriggersService.selectByPage(page, entity);
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:qrtzFiredTriggers:add")
    public Result add(@RequestBody QrtzFiredTriggers entity) {
        return this.qrtzFiredTriggersService.insert(entity).build(success("新增记录成功"));
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:qrtzFiredTriggers:edit")
    public Result edit(@RequestBody QrtzFiredTriggers entity) {
        return this.qrtzFiredTriggersService.updateById(entity).build(success("修改记录成功"));
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:qrtzFiredTriggers:detail")
    public Result detail(Long id) {
        return this.qrtzFiredTriggersService.getById(id).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:qrtzFiredTriggers:del")
    public Result del(@RequestBody List<Long> ids) {
        return this.qrtzFiredTriggersService.deleteByIds(ids).build(success("删除记录成功"));
    }

}

