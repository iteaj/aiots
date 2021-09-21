package com.iteaj.msn.core.controller;

import java.util.List;

import com.iteaj.msn.core.entity.MsgSend;
import com.iteaj.msn.core.service.IMsgSendService;
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
@RequestMapping("/core/msgSend")
public class MsgSendController extends BaseController {

    @Autowired
    private IMsgSendService msgSendService;

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:msgSend:view")
    public Result view(MsgSend entity, Page page) {
        return this.msgSendService.page(page, entity);
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:msgSend:add")
    public Result add(@RequestBody MsgSend entity) {
        return this.msgSendService.save(entity);
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:msgSend:edit")
    public Result edit(@RequestBody MsgSend entity) {
        return this.msgSendService.updateById(entity);
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:msgSend:detail")
    public Result detail(Long id) {
        return this.msgSendService.getById(id);
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:msgSend:del")
    public Result del(@RequestBody List<Long> ids) {
        return this.msgSendService.removeByIds(ids);
    }

}

