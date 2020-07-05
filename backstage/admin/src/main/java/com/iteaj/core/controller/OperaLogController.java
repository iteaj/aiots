package com.iteaj.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.core.entity.OperaLog;
import com.iteaj.core.service.IOperaLogService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/core/log")
public class OperaLogController extends BaseController {

    private final IOperaLogService operaLogService;

    public OperaLogController(IOperaLogService operaLogService) {
        this.operaLogService = operaLogService;
    }

    @GetMapping
    @RequiresPermissions("core:log:view")
    public String view() {
        return "log";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:log:view")
    public Result view(Page page, OperaLog operaLog) {
        return this.operaLogService.selectByPage(page, operaLog).response(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:log:del")
    public Result del(@RequestBody List<Long> list) {
        return this.operaLogService.deleteByIds(list).response(success());
    }
}
