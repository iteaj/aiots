package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.dto.PanelsDetailDto;
import com.iteaj.iboot.module.iot.entity.Signal;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.service.ISignalService;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
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

    private final ISignalService signalService;
    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IWarnRecordService warnRecordService;
    private final RealtimeDataService realtimeDataService;

    public PanelsController(ISignalService signalService, IDeviceService deviceService
            , IProductService productService, IWarnRecordService warnRecordService, RealtimeDataService realtimeDataService) {
        this.signalService = signalService;
        this.deviceService = deviceService;
        this.productService = productService;
        this.warnRecordService = warnRecordService;
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

                        Map<String, RealtimeData> realtimeDataMap = realtimeDataService.listOfDevice(productDto.getProtocolCode()
                                , DeviceKey.build(item.getDeviceSn(), item.getParentDeviceSn()));
                        if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                            productDto.getAttrs().stream().filter(attr -> attr.getAttrType() == AttrType.R).forEach(attr -> {
                                if(realtimeDataMap != null) {
                                    RealtimeData realtimeData = realtimeDataMap.get(attr.getField());
                                    if(realtimeData != null && realtimeData.getRealtime() != null) {
                                        SignalOrFieldValue realtime = realtimeData.getRealtime();
                                        debugDto.addAttr(attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType()
                                                , realtime.getValue(), realtime.getCollectTime(), realtime.getStatus());
                                        return;
                                    }
                                }

                                debugDto.addAttr(attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType());
                            });
                        }

                        productDto.setAttrs(null);

                        // 处理点位数据
                        signalService.list(Wrappers.<Signal>lambdaQuery().eq(Signal::getProductId, productDto.getId()))
                                .stream().forEach(signal -> {
                            if(realtimeDataMap != null) {
                                RealtimeData realtimeData = realtimeDataMap.get(signal.getAddress());
                                if(realtimeData != null && realtimeData.getRealtime() != null) {
                                    SignalOrFieldValue realtime = realtimeData.getRealtime();
                                    debugDto.addSignal(signal.getName(), signal.getAddress(), realtime.getValue()
                                            , realtime.getCollectTime(), realtime.getStatus());
                                    return;
                                }
                            }

                            debugDto.addSignal(signal.getName(), signal.getAddress());
                        });

                        WarnRecord entity = new WarnRecord(item.getUid());
                        warnRecordService.detailPage(new Page<WarnRecord>(1, 30), entity)
                                .<WarnRecord>stream().forEach(record -> {
                            debugDto.addWarn(record.getName(), record.getGrade(), record.getContent(), record.getWarnTime());
                        });
                    });
        });

        return success(debugDto);
    }
}
