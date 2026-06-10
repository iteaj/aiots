package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 移动端告警
 */
@RestController
@RequestMapping("/app/alarm")
public class AppAlarmController extends BaseController {

    private final IWarnRecordService warnRecordService;

    public AppAlarmController(IWarnRecordService warnRecordService) {
        this.warnRecordService = warnRecordService;
    }

    /**
     * 今日告警列表（按设备）
     */
    @GetMapping("/today")
    public Result<List<WarnRecord>> today(@RequestParam String deviceSn) {
        Date todayStart = DateUtil.beginOfDay(new Date());
        Date todayEnd = DateUtil.endOfDay(new Date());

        LambdaQueryWrapper<WarnRecord> wrapper = new LambdaQueryWrapper<WarnRecord>()
                .eq(WarnRecord::getDeviceSn, deviceSn)
                .ge(WarnRecord::getWarnTime, todayStart)
                .le(WarnRecord::getWarnTime, todayEnd)
                .orderByDesc(WarnRecord::getWarnTime);

        List<WarnRecord> list = warnRecordService.list(wrapper).getData();
        return success(list);
    }
}
