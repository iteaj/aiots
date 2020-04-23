package com.iteaj.admin.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.admin.entity.Config;
import com.iteaj.admin.entity.OperaLog;
import com.iteaj.admin.service.IConfigService;
import com.iteaj.admin.service.IOperaLogService;
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

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private IOperaLogService operaLogService;

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
