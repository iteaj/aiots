package com.iteaj.iboot.msn.quartz.controller;

import java.util.List;

import com.iteaj.iboot.msn.quartz.entity.QrtzJobDetails;
import com.iteaj.iboot.msn.quartz.service.IQrtzJobDetailsService;
import com.iteaj.framework.result.Result;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@RequestMapping("/develop/qrtzJobDetails")
public class QrtzJobDetailsController extends BaseController {

    private final IQrtzJobDetailsService qrtzJobDetailsService;

    public QrtzJobDetailsController(IQrtzJobDetailsService qrtzJobDetailsService) {
        this.qrtzJobDetailsService = qrtzJobDetailsService;
    }

    @GetMapping
    @RequiresPermissions("core:qrtzJobDetails:view")
    public String view() {
        return "qrtzJobDetails";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:qrtzJobDetails:view")
    public Result view(QrtzJobDetails entity, Page page) {
        return this.qrtzJobDetailsService.page(page, entity);
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:qrtzJobDetails:add")
    public Result add(@RequestBody QrtzJobDetails entity) {
        return this.qrtzJobDetailsService.save(entity);
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:qrtzJobDetails:edit")
    public Result edit(@RequestBody QrtzJobDetails entity) {
        return this.qrtzJobDetailsService.updateById(entity);
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:qrtzJobDetails:detail")
    public Result detail(Long id) {
        return this.qrtzJobDetailsService.getById(id);
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:qrtzJobDetails:del")
    public Result del(@RequestBody List<Long> ids) {
        return this.qrtzJobDetailsService.removeByIds(ids);
    }

}

