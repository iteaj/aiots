package com.iteaj.msn.quartz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.msn.quartz.entity.QrtzFiredTriggers;
import com.iteaj.msn.quartz.service.IQrtzFiredTriggersService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@RestController
@RequestMapping("/quartz/firedTriggers")
public class QrtzFiredTriggersController extends BaseController {

    private final IQrtzFiredTriggersService qrtzFiredTriggersService;

    public QrtzFiredTriggersController(IQrtzFiredTriggersService qrtzFiredTriggersService) {
        this.qrtzFiredTriggersService = qrtzFiredTriggersService;
    }

    /**
     * 查询出触发记录
     * @param entity
     * @param page
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:qrtzFiredTriggers:view")
    public Result<IPage<QrtzFiredTriggers>> view(QrtzFiredTriggers entity, Page page) {
        return this.qrtzFiredTriggersService.page(page, entity);
    }

    @PostMapping("/add")
    @RequiresPermissions("core:qrtzFiredTriggers:add")
    public Result<Boolean> add(@RequestBody QrtzFiredTriggers entity) {
        return this.qrtzFiredTriggersService.save(entity);
    }

    @PostMapping("/edit")
    @RequiresPermissions("core:qrtzFiredTriggers:edit")
    public Result<Boolean> edit(@RequestBody QrtzFiredTriggers entity) {
        return this.qrtzFiredTriggersService.updateById(entity);
    }

    @GetMapping("/edit")
    @RequiresPermissions("core:qrtzFiredTriggers:edit")
    public Result<QrtzFiredTriggers> edit(Long id) {
        return this.qrtzFiredTriggersService.getById(id);
    }

    @PostMapping("/del")
    @RequiresPermissions("core:qrtzFiredTriggers:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.qrtzFiredTriggersService.removeByIds(ids);
    }

}

