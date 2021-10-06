package com.iteaj.msn.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.msn.core.entity.AccessLog;
import com.iteaj.msn.core.service.IAccessLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/log")
public class OperaLogController extends BaseController {

    private final IAccessLogService operaLogService;

    public OperaLogController(IAccessLogService operaLogService) {
        this.operaLogService = operaLogService;
    }

    /**
     * 获取日志记录
     * @param page
     * @param accessLog
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:log:view")
    public Result view(Page page, AccessLog accessLog) {
        return this.operaLogService.page(page, accessLog);
    }

    /**
     * 删除日志记录
     * @param list
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions("core:log:del")
    public Result del(@RequestBody List<Long> list) {
        return this.operaLogService.removeByIds(list);
    }
}
