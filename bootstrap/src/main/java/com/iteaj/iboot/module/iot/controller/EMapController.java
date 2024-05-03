package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.EMapDeviceDto;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 电子地图模块
 */
@RestController
@RequestMapping("/iot/emap")
public class EMapController extends BaseController {

    private final IDeviceService deviceService;

    public EMapController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 获取设备列表
     * @param productId 产品id
     * @param deviceGroupId 设备组id
     * @return
     */
    @GetMapping("devices")
    public Result<List<EMapDeviceDto>> devices(Long productId, Long deviceGroupId) {
        List<EMapDeviceDto> devices = deviceService.listOfEMap(productId, deviceGroupId);
        return success(devices);
    }
}
