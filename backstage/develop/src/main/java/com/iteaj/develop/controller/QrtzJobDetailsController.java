package com.iteaj.develop.controller;

import java.util.List;
import com.iteaj.util.Result;
import com.iteaj.develop.entity.QrtzJobDetails;
import com.iteaj.develop.service.IQrtzJobDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IQrtzJobDetailsService qrtzJobDetailsService;

    @GetMapping
    @RequiresPermissions("core:qrtzJobDetails:view")
    public String view() {
        return "qrtzJobDetails";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:qrtzJobDetails:view")
    public Result view(QrtzJobDetails entity, Page page) {
        return this.qrtzJobDetailsService.selectByPage(page, entity).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:qrtzJobDetails:add")
    public Result add(@RequestBody QrtzJobDetails entity) {
        return this.qrtzJobDetailsService.insert(entity).build(success("新增记录成功"));
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:qrtzJobDetails:edit")
    public Result edit(@RequestBody QrtzJobDetails entity) {
        return this.qrtzJobDetailsService.updateById(entity).build(success("修改记录成功"));
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:qrtzJobDetails:detail")
    public Result detail(Long id) {
        return this.qrtzJobDetailsService.getById(id).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:qrtzJobDetails:del")
    public Result del(@RequestBody List<Long> ids) {
        return this.qrtzJobDetailsService.deleteByIds(ids).build(success("删除记录成功"));
    }

}

