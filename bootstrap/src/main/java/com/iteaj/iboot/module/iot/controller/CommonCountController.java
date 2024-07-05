package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.CollectWarnDataService;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.framework.spi.iot.data.EchartsCountValue;
import com.iteaj.iboot.module.iot.dto.DeviceStatusDto;
import com.iteaj.iboot.module.iot.dto.StatusCountCondition;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通用数据统计管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/iot/common/count")
public class CommonCountController extends BaseController {

    private final IDeviceService deviceService;
    private final CollectWarnDataService collectWarnDataService;
    private final RealtimeCollectDataService collectDataService;

    /**
     * 采集数据统计
     * @param condition
     * @return
     */
    @PostMapping("data")
    public Result<List<EchartsCountValue>> commonDataCount(@RequestBody TimeCountCondition condition) {
        List<EchartsCountValue> baseTimeCounts = collectDataService.countOfCommonByTime(condition);
        return success(baseTimeCounts);
    }

    /**
     * 告警数据统计
     * @param condition
     * @return
     */
    @PostMapping("warn")
    public Result<List<EchartsCountValue>> commonWarnCount(@RequestBody TimeCountCondition condition) {
        List<EchartsCountValue> echartsCountValues = collectWarnDataService.countOfCommonByTime(condition);
        return success(echartsCountValues);
    }

    /**
     * 设备当前状态统计
     * @param condition
     * @return
     */
    @PostMapping("status")
    public Result<List<EchartsCountValue>> statusCount(@RequestBody StatusCountCondition condition) {
        List<EchartsCountValue> values = deviceService.countCommonDeviceStatus(condition);
        return success(values);
    }
}
