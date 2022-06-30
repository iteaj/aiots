package com.iteaj.iboot.msn.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.core.dto.OnlineCountDto;
import com.iteaj.iboot.msn.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统功能统计
 */
@RestController
@RequestMapping("/iot/statistics")
public class IotStatisticsController extends BaseController {

    private final IDeviceService deviceService;

    public IotStatisticsController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }


    /**
     * 当前设备总数和当前在线数
     * @return
     */
    @GetMapping("/device/current")
    public Result<CurrentDeviceDto> current() {
        return success(deviceService.countCurrentDevice());
    }


}
