package com.iteaj.modules.core.controller;

import java.util.List;
import com.iteaj.util.Result;
import com.iteaj.core.entity.MsgSend;
import com.iteaj.core.service.IMsgSendService;
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
@RequestMapping("/core/msgSend")
public class MsgSendController extends BaseController {

    @Autowired
    private IMsgSendService msgSendService;

    @GetMapping
    @RequiresPermissions("core:msgSend:view")
    public String view() {
        return "msgSend";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:msgSend:view")
    public Result view(MsgSend entity, Page page) {
        return this.msgSendService.selectByPage(page, entity).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:msgSend:add")
    public Result add(@RequestBody MsgSend entity) {
        return this.msgSendService.insert(entity).build(success("新增记录成功"));
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:msgSend:edit")
    public Result edit(@RequestBody MsgSend entity) {
        return this.msgSendService.updateById(entity).build(success("修改记录成功"));
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:msgSend:detail")
    public Result detail(Long id) {
        return this.msgSendService.getById(id).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:msgSend:del")
    public Result del(@RequestBody List<Long> ids) {
        return this.msgSendService.deleteByIds(ids).build(success("删除记录成功"));
    }

}

