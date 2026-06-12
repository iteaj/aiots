package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.WarnRecordVo;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端告警记录
 */
@RestController
@RequestMapping("/app/warnRecord")
public class AppWarnRecordController extends BaseController {

    private final IWarnRecordService warnRecordService;

    public AppWarnRecordController(IWarnRecordService warnRecordService) {
        this.warnRecordService = warnRecordService;
    }

    /**
     * 今日告警列表（按设备）
     */
    @GetMapping("/today")
    public Result<List<WarnRecordVo>> today(@RequestParam String uid) {
        Date todayStart = DateUtil.beginOfDay(new Date());
        Date todayEnd = DateUtil.endOfDay(new Date());

        List<WarnRecord> list = warnRecordService.listByUidAndTimeRange(uid, todayStart, todayEnd);

        List<WarnRecordVo> voList = list.stream()
                .map(WarnRecordVo::of)
                .collect(Collectors.toList());

        return success(voList);
    }

    /**
     * 告警记录分页（按设备）
     */
    @GetMapping("/page")
    public Result<IPage<WarnRecordVo>> page(Page<WarnRecord> page, @RequestParam String uid) {
        PageResult<IPage<WarnRecord>> pageResult = warnRecordService.pageByUid(page, uid);
        IPage<WarnRecord> data = pageResult.getData();

        IPage<WarnRecordVo> voPage = new Page<>(data.getCurrent(), data.getSize(), data.getTotal());
        voPage.setRecords(data.getRecords().stream()
                .map(WarnRecordVo::of)
                .collect(Collectors.toList()));

        return success(voPage);
    }
}
