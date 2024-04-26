package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 大屏接口管理
 */
@RestController
@RequestMapping("/iot/vls")
public class VLScreenController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final ILinkageService linkageService;
    private final IWarnRecordService warnRecordService;
    private final IDeviceGroupService deviceGroupService;
    private final IEventSourceService eventSourceService;
    private final ICollectDataService collectDataService;

    public VLScreenController(IDeviceService deviceService, IProductService productService
            , ILinkageService linkageService, IWarnRecordService warnRecordService
            , IDeviceGroupService deviceGroupService, IEventSourceService eventSourceService
            , ICollectDataService collectDataService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.linkageService = linkageService;
        this.warnRecordService = warnRecordService;
        this.deviceGroupService = deviceGroupService;
        this.eventSourceService = eventSourceService;
        this.collectDataService = collectDataService;
    }

    /**
     * 设备组列表
     * @return
     */
    @GetMapping("/groups")
    public Result<List<IVOption>> groups() {
        List<IVOption> options = this.deviceGroupService.list().stream()
                .map(item -> new IVOption(item.getName(), item.getId()))
                .collect(Collectors.toList());
        options.add(0, new IVOption("请选择设备分组", null));
        return success(options);
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

    /**
     * 获取告警列表
     * @param param
     * @return
     */
    @GetMapping("warns")
    public Result<List<WarnRecord>> warns(GroupAndProductParamDto param) {
        List<WarnRecord> warnRecords = warnRecordService.listByDeviceGroupId(param.getGroupId(), 8);
        return success(warnRecords);
    }

    /**
     * 近七天告警列表
     * @param deviceGroupId 设备分组id
     * @return
     */
    @GetMapping("lastWeekWarns")
    public Result<List<CountLastWeekDto>> lastWeekWarns(Long deviceGroupId) {
        List<CountLastWeekDto> dtos = warnRecordService.countLastWeek(deviceGroupId);
        return success(dtos);
    }

    /**
     * 近七天采集数据列表
     * @param deviceGroupId 设备分组id
     * @return
     */
    @GetMapping("lastWeekData")
    public Result<List<CountLastWeekDto>> lastWeekData(Long deviceGroupId) {
        List<CountLastWeekDto> dtos = collectDataService.countLastWeek(deviceGroupId);
        return success(dtos);
    }

    /**
     * 获取监控设备列表
     * @return
     */
    @GetMapping("video")
    public Result<List<String>> video(Long deviceGroupId) {
        Product product = productService.getOne(Wrappers.<Product>lambdaQuery().eq(Product::getCode, "dahua_video_device"))
                .ifNotPresentThrow("没有找到大华监控产品").getData();
        List<String> channelCodes = this.deviceService.list(Wrappers.<Device>lambdaQuery()
                .eq(Device::getProductId, product.getId())
                .eq(deviceGroupId != null, Device::getDeviceGroupId, deviceGroupId))
                .stream().map(item -> item.getDeviceSn()).collect(Collectors.toList());
        return success(channelCodes);
    }
}
