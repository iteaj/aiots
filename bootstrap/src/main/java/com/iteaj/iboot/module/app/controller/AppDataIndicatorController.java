package com.iteaj.iboot.module.app.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.IndicatorStats;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.service.IDataIndicatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 移动端数据指标接口
 */
@RestController
@RequestMapping("/m/dataIndicator")
public class AppDataIndicatorController extends BaseController {

    private final IDataIndicatorService dataIndicatorService;

    public AppDataIndicatorController(IDataIndicatorService dataIndicatorService) {
        this.dataIndicatorService = dataIndicatorService;
    }

    /**
     * 获取点位在指定时间范围内的聚合统计（max/maxTime/min/minTime/avg/count）
     */
    @GetMapping("stats")
    public Result<IndicatorStats> stats(String uid, Long fieldId, String start, String end) {
        return success(dataIndicatorService.getStats(uid, fieldId, start, end));
    }

    /**
     * 获取点位在指定时间范围内的采集数据列表
     */
    @GetMapping("listData")
    public Result<List<CollectData>> listData(String uid, Long fieldId, String start, String end) {
        return success(dataIndicatorService.listData(uid, fieldId, start, end));
    }

    /**
     * 获取设备其他点位在同一采集时间的数据
     */
    @GetMapping("associatedPoints")
    public Result<List<CollectData>> associatedPoints(String uid, Long excludeFieldId, String collectTime) {
        return success(dataIndicatorService.associatedPoints(uid, excludeFieldId, collectTime));
    }

    /**
     * 获取同产品下其他设备同点位在同一采集时间的数据
     */
    @GetMapping("crossDevicePoints")
    public Result<List<CollectData>> crossDevicePoints(String productCode, Long fieldId, String collectTime, String excludeUid) {
        return success(dataIndicatorService.crossDevicePoints(productCode, fieldId, collectTime, excludeUid));
    }
}
