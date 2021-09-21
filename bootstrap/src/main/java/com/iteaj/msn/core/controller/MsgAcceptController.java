package com.iteaj.msn.core.controller;

import java.util.List;

import com.iteaj.msn.core.entity.MsgAccept;
import com.iteaj.msn.core.service.IMsgAcceptService;
import com.iteaj.framework.result.Result;
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
 * @since 2020-07-07
 */
@Controller
@RequestMapping("/core/msgAccept")
public class MsgAcceptController extends BaseController {

    @Autowired
    private IMsgAcceptService msgAcceptService;

    @GetMapping
    @RequiresPermissions("core:msgAccept:view")
    public String view() {
        return "msgAccept";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:msgAccept:view")
    public Result view(MsgAccept entity, Page page) {
        return this.msgAcceptService.page(page, entity).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:msgAccept:add")
    public Result add(@RequestBody MsgAccept entity) {
        return this.msgAcceptService.save(entity).build(success("新增记录成功"));
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:msgAccept:edit")
    public Result edit(@RequestBody MsgAccept entity) {
        return this.msgAcceptService.updateById(entity).build(success("修改记录成功"));
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:msgAccept:detail")
    public Result detail(Long id) {
        return this.msgAcceptService.getById(id).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:msgAccept:del")
    public Result del(@RequestBody List<Long> ids) {
        return this.msgAcceptService.removeByIds(ids).build(success("删除记录成功"));
    }

}

