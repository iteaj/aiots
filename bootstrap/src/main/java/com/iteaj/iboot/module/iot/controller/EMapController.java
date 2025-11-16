package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.dto.EMapDetailDto;
import com.iteaj.iboot.module.iot.dto.EMapDeviceDto;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 电子地图模块
 */
@RestController
@RequestMapping("/iot/emap")
public class EMapController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IWarnRecordService warnRecordService;
    private final RealtimeDataService realtimeDataService;

    public EMapController(IDeviceService deviceService, IProductService productService
            , IWarnRecordService warnRecordService, RealtimeDataService realtimeDataService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.warnRecordService = warnRecordService;
        this.realtimeDataService = realtimeDataService;
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

    /**
     * 获取设备详情
     * @param uid
     * @return
     */
    @GetMapping("detail")
    public Result<EMapDetailDto> detail(String uid) {
        EMapDetailDto detailDto = new EMapDetailDto();
        deviceService.getByUid(uid).ifNotPresentThrow("设备不存在").ifPresent(item -> {
            productService.joinDetailById(item.getProductId())
                .ifNotPresentThrow("此设备数据不完整["+item.getName()+"]")
                .ifPresent(productDto -> {

                    Map<String, RealtimeData> realtimeDataMap = realtimeDataService.listOfDevice(productDto.getProtocolCode()
                            , DeviceKey.build(item.getDeviceSn(), item.getParentDeviceSn()));
                    if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                        productDto.getAttrs().stream().filter(attr -> (attr.getAttrType() != AttrType.W
                            && !DataType.t_json.getValue().equals(attr.getDataType()))
                                        && !DataType.t_bit.getValue().equals(attr.getDataType()))
                            .forEach(attr -> {
                                if(realtimeDataMap != null) {
                                    RealtimeData realtimeData = realtimeDataMap.get(attr.getField());
                                    if(realtimeData != null && realtimeData.getRealtime() != null) {
                                        SignalOrFieldValue realtime = realtimeData.getRealtime();
                                        detailDto.addAttr(attr.getField(), attr.getName(), attr.getUnit()
                                                , realtime.getValue(), realtime.getCollectTime(), realtime.getStatus());
                                        return;
                                    }
                                }

                                detailDto.addAttr(attr.getField(), attr.getName(), attr.getUnit());
                            });
                    }

                    WarnRecord entity = new WarnRecord(item.getUid());
                    warnRecordService.detailPage(new Page<WarnRecord>(1, 20), entity)
                            .<WarnRecord>stream().forEach(record -> {
                        detailDto.addWarn(record.getField(), record.getGrade(), record.getContent(), record.getWarnTime());
                    });
                });
        });

        return success(detailDto);
    }
}
