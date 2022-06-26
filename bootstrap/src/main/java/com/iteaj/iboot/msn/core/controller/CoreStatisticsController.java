package com.iteaj.iboot.msn.core.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.core.service.IOnlineUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统功能统计
 */
@RestController
@RequestMapping("/core/statistics")
public class CoreStatisticsController extends BaseController {

    private final IOnlineUserService onlineUserService;

    public CoreStatisticsController(IOnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    /**
     * 当天访问人数
     * @return
     */
    @GetMapping("/today/online")
    public Result<Long> todayOnline() {
        long num = onlineUserService.countTodayOnline();
        return success(onlineUserService.countTodayOnline());
    }

    /**
     * 近一个月在线用户统计
     * @return
     */
    @GetMapping("/online")
    public Result online() {
        return success();
    }
}
