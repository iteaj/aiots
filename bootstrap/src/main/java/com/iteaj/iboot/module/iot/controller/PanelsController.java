package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.dto.DebugDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.dto.PanelsDetailDto;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 运行展板模块
 */
@RestController
@RequestMapping("/iot/panels")
public class PanelsController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final RealtimeDataService realtimeDataService;

    public PanelsController(IDeviceService deviceService, IProductService productService, RealtimeDataService realtimeDataService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.realtimeDataService = realtimeDataService;
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

    /**
     * 调试详情
     * @param deviceId
     * @return
     */
    @GetMapping("detail")
    public Result<PanelsDetailDto> detail(Long deviceId) {
        PanelsDetailDto debugDto = new PanelsDetailDto();
        deviceService.detailById(deviceId)
                .ifNotPresentThrow("设备不存在").of().ifPresent(item -> {
            debugDto.setDevice(item);
            productService.debugById(item.getProductId())
                    .ifNotPresentThrow("此设备数据不完整["+item.getName()+"]")
                    .ifPresent(productDto -> {
                        debugDto.setProduct(productDto);
                        if(!CollectionUtils.isEmpty(productDto.getFuncApis())) {
                            productDto.getFuncApis().forEach(api -> debugDto.addApiDebugParam(api.getCode(), api.resolveDownApiConfig(productDto.getAttrs())));
                        }

                        if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                            Map<String, RealtimeData> realtimeDataMap = realtimeDataService.listOfDevice(productDto.getProtocolCode()
                                    , DeviceKey.build(item.getDeviceSn(), item.getParentDeviceSn()));
                            productDto.getAttrs().forEach(attr -> {
                                if(realtimeDataMap != null) {
                                    RealtimeData realtimeData = realtimeDataMap.get(attr.getField());
                                    if(realtimeData != null && realtimeData.getRealtime() != null) {
                                        SignalOrFieldValue realtime = realtimeData.getRealtime();
                                        debugDto.addAttr(attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType(), realtime.getValue(), realtime.getCollectTime());
                                    } else {
                                        debugDto.addAttr(attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType(), "-", null);
                                    }
                                } else {
                                    debugDto.addAttr(attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType(), "-", null);
                                }
                            });
                        }
                    });
        });

        return success(debugDto);
    }
}
