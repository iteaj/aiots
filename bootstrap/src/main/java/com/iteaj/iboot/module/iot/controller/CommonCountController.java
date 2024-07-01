package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseTimeCount;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用数据统计管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/iot/common/count")
public class CommonCountController extends BaseController {

    private final RealtimeCollectDataService collectDataService;

    /**
     * 采集数据统计
     * @param condition
     * @return
     */
    @GetMapping("data")
    public Result<List<BaseTimeCount>> commonDataCount(TimeCountCondition condition) {
        List<BaseTimeCount> baseTimeCounts = collectDataService.countByTime(condition);
        return success(baseTimeCounts);
    }

    /**
     * 告警数据统计
     * @param condition
     * @return
     */
    @GetMapping("warn")
    public Result<List<BaseTimeCount>> commonWarnCount(TimeCountCondition condition) {

        return success();
    }
}
