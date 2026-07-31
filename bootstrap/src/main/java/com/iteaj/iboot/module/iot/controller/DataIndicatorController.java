package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.data.RealtimeDataParam;
import com.iteaj.iboot.module.iot.dto.AssociatedStats;
import com.iteaj.framework.spi.iot.data.IndicatorStats;
import com.iteaj.iboot.module.iot.dto.IndicatorCardVO;

import com.iteaj.iboot.module.iot.service.IDataIndicatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端数据指标接口
 */
@RestController
@RequestMapping("/iot/dataIndicator")
public class DataIndicatorController extends BaseController {

    private final IDataIndicatorService dataIndicatorService;

    public DataIndicatorController(IDataIndicatorService dataIndicatorService) {
        this.dataIndicatorService = dataIndicatorService;
    }

    /**
     * 分页获取采集历史数据（数据指标卡片列表）
     */
    @GetMapping("view")
    public Result<Page<IndicatorCardVO>> view(Page page, RealtimeDataParam param) {
        return success(dataIndicatorService.viewOfPage(page, param));
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
    public Result<List<AssociatedStats>> listData(String uid, Long fieldId, String start, String end) {
        return success(dataIndicatorService.listData(uid, fieldId, start, end));
    }

    /**
     * 获取设备其他点位在同一采集时间的数据
     */
    @GetMapping("associatedPoints")
    public Result<List<AssociatedStats>> associatedPoints(String uid, Long excludeFieldId, String collectTime) {
        return success(dataIndicatorService.associatedPoints(uid, excludeFieldId, collectTime));
    }

    /**
     * 获取同产品下其他设备同点位在同一采集时间的数据
     */
    @GetMapping("crossDevicePoints")
    public Result<List<AssociatedStats>> crossDevicePoints(Long fieldId, String collectTime, String excludeUid) {
        return success(dataIndicatorService.crossDevicePoints(fieldId, collectTime, excludeUid));
    }

    /**
     * 获取与指定点位相关联的其他点位信息
     * <p>关联维度：同一采集器(cid)、同一网关(puid)、同一设备组(deviceGroupId)</p>
     * @param uid         源设备标识
     * @param fieldId     源属性ID
     * @param collectTime 采集时间（精确到秒）
     * @param grade       告警等级（可选）
     */
    @GetMapping("associatedStats")
    public Result<List<AssociatedStats>> associatedStats(String uid, Long fieldId, String collectTime, String grade) {
        return success(dataIndicatorService.associatedStats(uid, fieldId, collectTime, grade));
    }
}
