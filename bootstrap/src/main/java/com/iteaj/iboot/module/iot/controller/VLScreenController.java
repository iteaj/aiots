package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.DeviceStatusDto;
import com.iteaj.iboot.module.iot.dto.FuncStatusProfileDto;
import com.iteaj.iboot.module.iot.dto.GroupAndProductParamDto;
import com.iteaj.iboot.module.iot.dto.VlsDeviceAndProductDto;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IEventSourceService;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大屏接口管理
 */
@RestController
@RequestMapping("/iot/vls")
public class VLScreenController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final ILinkageService linkageService;
    private final IEventSourceService eventSourceService;

    public VLScreenController(IDeviceService deviceService, IProductService productService
            , ILinkageService linkageService, IEventSourceService eventSourceService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.linkageService = linkageService;
        this.eventSourceService = eventSourceService;
    }

    /**
     * 统计设备状态, 产品、事件源、场景联动信息
     * @return
     */
    @GetMapping("countDeviceAndProduct")
    public Result<VlsDeviceAndProductDto> countDeviceAndProduct(GroupAndProductParamDto param) {
        DeviceStatusDto deviceStatus = deviceService.countDeviceStatus(param);
        FuncStatusProfileDto productProfileDto = productService.countStatusProfile();
        FuncStatusProfileDto profileDto = linkageService.countStatusProfile(param);
        FuncStatusProfileDto esProfileDto = eventSourceService.countStatusProfile(param);
        return success(new VlsDeviceAndProductDto()
                .setOfflineNum(deviceStatus.getOfflineNum())
                .setOnlineNum(deviceStatus.getOnlineNum())
                .setRunningEventSourceNum(esProfileDto.getEnabledNum())
                .setTotalEventSourceNum(esProfileDto.getTotalNum())
                .setTotalLinkageNum(profileDto.getTotalNum())
                .setRunningLinkageNum(profileDto.getEnabledNum())
                .setEnabledProductNum(productProfileDto.getEnabledNum())
                .setTotalProductNum(productProfileDto.getTotalNum()));
    }
}
