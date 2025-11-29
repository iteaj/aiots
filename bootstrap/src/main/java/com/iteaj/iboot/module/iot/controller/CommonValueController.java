package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.data.CollectDataCondition;
import com.iteaj.framework.spi.iot.data.CollectValueItem;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.iboot.module.iot.dto.StatusDataCondition;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用数据管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/iot/common/value")
public class CommonValueController extends BaseController {

    private final IDeviceService deviceService;
    private final RealtimeCollectDataService collectDataService;

    /**
     * 获取采集数据列表
     * @param condition
     * @return
     */
    @PostMapping("data")
    public Result<List<CollectValueItem>> data(@RequestBody CollectDataCondition condition) {
        List<CollectValueItem> valueItems = collectDataService.listOfCommonByTime(condition);
        return success(valueItems);
    }

    /**
     * 获取告警列表
     * @param condition
     * @return
     */
    @PostMapping("status")
    public Result<CollectValueItem> status(@RequestBody StatusDataCondition condition) {
        CollectValueItem item = deviceService.listCommonByStatus(condition);
        return success(item);
    }
}
