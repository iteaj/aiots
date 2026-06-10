package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.service.*;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 移动端设备管理
 */
@RestController
@RequestMapping("/app/device")
public class AppDeviceController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IModelApiService modelApiService;
    private final RealtimeDataService realtimeDataService;
    private final IWarnRecordService warnRecordService;
    private final IDeviceGroupService deviceGroupService;
    private final IModelApiInvokeService modelApiInvokeService;

    public AppDeviceController(IDeviceService deviceService, IProductService productService,
                               IModelApiService modelApiService, RealtimeDataService realtimeDataService,
                               IWarnRecordService warnRecordService, IDeviceGroupService deviceGroupService,
                               IModelApiInvokeService modelApiInvokeService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.modelApiService = modelApiService;
        this.realtimeDataService = realtimeDataService;
        this.warnRecordService = warnRecordService;
        this.deviceGroupService = deviceGroupService;
        this.modelApiInvokeService = modelApiInvokeService;
    }

    /**
     * 设备统计（在线/离线/总数）
     */
    @GetMapping("/statistics")
    public Result<AppDeviceStatisticsVo> statistics(Long deviceGroupId) {
        AppDeviceStatisticsVo vo = new AppDeviceStatisticsVo();

        // 查询设备状态统计
        if (deviceGroupId != null) {
            GroupAndProductParamDto param = new GroupAndProductParamDto();
            param.setGroupId(deviceGroupId);
            DeviceStatusDto statusDto = deviceService.countCommonDeviceStatus(param);
            vo.setOnline(statusDto.getOnlineNum());
            vo.setOffline(statusDto.getOfflineNum());
            vo.setTotal(statusDto.getOnlineNum() + statusDto.getOfflineNum());
        } else {
            CurrentDeviceDto current = deviceService.countCurrentDevice();
            vo.setOnline(current.getOnline());
            vo.setTotal(current.getToday());
            vo.setOffline(current.getToday() - current.getOnline());
        }

        return success(vo);
    }

    /**
     * 设备列表（卡片数据）
     */
    @GetMapping("/list")
    public Result<IPage<AppDeviceCardVo>> list(Page page, Long deviceGroupId, Long productId, String keyword, String status) {
        DeviceDto device = new DeviceDto();
        device.setName(keyword);
        device.setProductId(productId);
        device.setDeviceGroupId(deviceGroupId);
        if (status != null) {
            device.setStatus(DeviceStatus.getInstance(status));
        }
        device.setDeviceTypes(new DeviceType[]{DeviceType.Child, DeviceType.Direct});
        PageResult<IPage<DeviceDto>> pageResult = deviceService.pageOfDetail(page, device);

        Map<Long, String> productProtocolMap = new HashMap<>();
        Map<Long, List<CtrlCommandDto>> productOptionsMap = new HashMap<>();
        Map<Long, Map<String, String>> productAttrDictsMap = new HashMap<>();
        Map<Long, List<AppDeviceCardVo.AttrValueVo>> productValuesMap = new HashMap<>();

        IPage<DeviceDto> data = pageResult.getData();
        if(CollectionUtil.isNotEmpty(data.getRecords())) {
            List<Long> productIds = data.getRecords().stream()
                    .map(Device::getProductId).distinct()
                    .collect(Collectors.toList());
            List<ProductDto> list = productService.listCtrlStatusModelAttrs(productIds);
            if(CollectionUtil.isNotEmpty(list)) {
                list.forEach(productDto -> {
                    List<AppDeviceCardVo.AttrValueVo> values = new ArrayList<>();
                    productProtocolMap.put(productDto.getId(), productDto.getProtocolCode());
                    List<CtrlCommandDto> commandDtos = new ArrayList<>();
                    productDto.getFuncApis().forEach(modelApi -> {
                        modelApiService.detailByCode(modelApi.getCode()).ifPresent(ctrlModelApi -> {
                            if(ctrlModelApi != null) {
                                commandDtos.add(ctrlModelApi.resolverSimpleCommand());
                            }
                        });
                    });
                    productOptionsMap.put(productDto.getId(), commandDtos);

                    if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                        Map<String, String> attrDictMap = new HashMap<>();
                        productDto.getAttrs().forEach(attr -> {
                            if(attr.getAttrType() != AttrType.W) { // 读和读写
                                if(!DataType.isContainer(attr.getDataType())) {
                                    values.add(new AppDeviceCardVo.AttrValueVo(attr.getField(), attr.getName(), attr.getUnit()));
                                }
                            }

                            if(CollectionUtil.isNotEmpty(attr.getDicts())
                                    && !DataType.isContainer(attr.getDataType())) {
                                attr.getDicts().forEach(dict -> attrDictMap.put(attr.getField()+"_"+dict.getDictValue(), dict.getDictName()));
                            }
                        });

                        productValuesMap.put(productDto.getId(), values);
                        productAttrDictsMap.put(productDto.getId(), attrDictMap);
                    }
                });

                List<AppDeviceCardVo> panelsDeviceDtos = new ArrayList<>();
                data.getRecords().forEach(deviceDto -> {
                    List<CtrlCommandDto> ctrlCommandDtos = productOptionsMap.get(deviceDto.getProductId());
                    Map<String, String> dictStringMap = productAttrDictsMap.get(deviceDto.getProductId());
                    List<AppDeviceCardVo.AttrValueVo> attrValues = productValuesMap.get(deviceDto.getProductId());

                    AppDeviceCardVo panelsDeviceDto = new AppDeviceCardVo(ctrlCommandDtos, Collections.emptyList()
                            , deviceDto.getName(), deviceDto.getDeviceSn(), deviceDto.getUid(), deviceDto.getStatus() == DeviceStatus.online);
//                    // 获取每个属性的实时数据
                    if(CollectionUtil.isNotEmpty(attrValues)) {
                        if(CollectionUtil.isNotEmpty(ctrlCommandDtos)) {
                            for (CtrlCommandDto ctrlCommandDto : ctrlCommandDtos) {
                                if (ctrlCommandDto.getAttrField() != null) {
                                    ModelAttrRealtimeData realtimeData = realtimeDataService.getRealtimeData(deviceDto.getUid(), ctrlCommandDto.getAttrField());
                                    if(realtimeData != null && realtimeData.getRealtime() != null) {
                                        panelsDeviceDto.putCtrlValue(ctrlCommandDto.getAttrField(), realtimeData.getRealtime().getValue().toString());
                                    }
                                }
                            }
                        }

                        List<AppDeviceCardVo.AttrValueVo> copyAttrValues = attrValues.stream()
                                .map(item -> new AppDeviceCardVo.AttrValueVo(item.getField(), item.getName(), item.getUnit()))
                                .collect(Collectors.toList());

                        copyAttrValues.forEach(attrValue -> {
                            String protocolCode = productProtocolMap.get(deviceDto.getProductId());
                            Map<String, RealtimeData> realtimeDataMap = realtimeDataService.listOfDevice(protocolCode
                                    , DeviceKey.build(deviceDto.getDeviceSn(), deviceDto.getParentDeviceSn()));
                            if(CollectionUtil.isNotEmpty(realtimeDataMap)) {

                                // 设置各属性的实时值
                                RealtimeData realtimeData = realtimeDataMap.get(attrValue.getField());
                                if(realtimeData != null && realtimeData.getRealtime() != null
                                        && realtimeData.getRealtime().getValue() != null) {

                                    // 设置设备最新的采集时间
                                    if(panelsDeviceDto.getCollectTime() == null) {
                                        panelsDeviceDto.setCollectTime(realtimeData.getRealtime().getCollectTime());
                                    } else {
                                        boolean b = panelsDeviceDto.getCollectTime().getTime() > realtimeData.getRealtime().getCollectTime().getTime();
                                        panelsDeviceDto.setCollectTime(b ? panelsDeviceDto.getCollectTime() : realtimeData.getRealtime().getCollectTime());
                                    }

                                    // 将采集到的值转成字典label
                                    attrValue.setValue(realtimeData.getRealtime().getValue().toString());
                                    attrValue.setLabel(dictStringMap.get(attrValue.getField() + "_" + attrValue.getValue()));
                                }
                            }
                        });

                        panelsDeviceDto.setValues(copyAttrValues);
                    }

                    panelsDeviceDtos.add(panelsDeviceDto);
                });

                page.setRecords(panelsDeviceDtos);
            }
        }
        return success(page);
    }

    /**
     * 设备分组列表
     */
    @GetMapping("/groups")
    public Result<List<DeviceGroup>> groups() {
        return this.deviceGroupService.list();
    }

    /**
     * 控制状态切换
     */
    @PostMapping("/command/invoke/update")
    public Mono<Result<?>> switchCtrlStatus(@RequestBody CmdInvokeUpdateDto dto) {
        return modelApiInvokeService.invokeByEvent(dto).map(result -> {
            if (result.getStatus() == ExecStatus.success) {
                return success("指令下发成功");
            } else {
                return fail(result.getReason());
            }
        });
    }

    /**
     * 属性定义（内部使用）
     */
    private static class AttrDef {
        final String field;
        final String name;
        final String unit;
        final Boolean ctrlStatus;

        AttrDef(String field, String name, String unit, Boolean ctrlStatus) {
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.ctrlStatus = ctrlStatus;
        }
    }
}
