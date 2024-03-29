package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 运行展板模块
 */
@RestController
@RequestMapping("/iot/panels")
public class PanelsController extends BaseController {

    private final IDeviceService deviceService;

    public PanelsController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 设备列表
     * @param deviceGroupId 设备分组
     * @return
     */
    @GetMapping("/devices")
    public Result<IPage<DeviceDto>> devices(Page page, Long deviceGroupId, Long productId) {
        DeviceDto device = new DeviceDto();
        device.setProductId(productId);
        device.setDeviceGroupId(deviceGroupId);
        return deviceService.pageOfDetail(page, device);
    }
}
