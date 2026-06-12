package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.data.ModelAttrDictMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.service.*;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.Serializable;
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
    private final IDeviceGroupService deviceGroupService;
    private final IModelAttrService modelAttrService;
    private final IModelApiInvokeService modelApiInvokeService;
    private final IDeviceCmdLogService deviceCmdLogService;

    public AppDeviceController(IDeviceService deviceService, IProductService productService,
                               IModelApiService modelApiService, RealtimeDataService realtimeDataService,
                               IDeviceGroupService deviceGroupService,
                               IModelAttrService modelAttrService, IModelApiInvokeService modelApiInvokeService,
                               IDeviceCmdLogService deviceCmdLogService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.modelApiService = modelApiService;
        this.realtimeDataService = realtimeDataService;
        this.deviceGroupService = deviceGroupService;
        this.modelAttrService = modelAttrService;
        this.modelApiInvokeService = modelApiInvokeService;
        this.deviceCmdLogService = deviceCmdLogService;
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
        PageResult<IPage<DeviceDto>> pageResult = deviceService.listByApp(page, device);

        Map<Long, String> productProtocolMap = new HashMap<>();
        Map<Long, List<CtrlCommandDto>> productOptionsMap = new HashMap<>();
        Map<Long, Map<String, String>> productAttrDictsMap = new HashMap<>();
        Map<Long, List<AppDeviceCardVo.AttrValueVo>> productValuesMap = new HashMap<>();

        IPage<DeviceDto> data = pageResult.getData();
        if(CollectionUtil.isNotEmpty(data.getRecords())) {
            data.getRecords().sort(Comparator.comparing((deviceDto) -> {
                switch (deviceDto.getDeviceType()) {
                    case Child:
                        return 0;
                    case Direct:
                        return 1;
                    default:
                        return 2;
                }
            }));
            List<Long> productIds = data.getRecords().stream()
                    .filter(item->item.getDeviceType() != DeviceType.Gateway)
                    .map(Device::getProductId).distinct()
                    .collect(Collectors.toList());
            // 网关子设备和直连设备产品
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
                    if(deviceDto.getDeviceType() != DeviceType.Gateway) {
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
                    } else {
                        panelsDeviceDtos.add(new AppDeviceCardVo(Collections.emptyList(), Collections.emptyList()
                                , deviceDto.getName(), deviceDto.getDeviceSn(), deviceDto.getUid()
                                , deviceDto.getStatus() == DeviceStatus.online));
                    }
                });

                page.setRecords(panelsDeviceDtos);
            } else { // 网关设备产品
                List<AppDeviceCardVo> appDeviceCardVos = data.getRecords().stream().map(deviceDto ->
                        new AppDeviceCardVo(Collections.emptyList(), Collections.emptyList()
                        , deviceDto.getName(), deviceDto.getDeviceSn(), deviceDto.getUid()
                        , deviceDto.getStatus() == DeviceStatus.online)).toList();
                page.setRecords(appDeviceCardVos);
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
     * 设备指令集列表（移动端，只返回功能指令）
     */
    @GetMapping("/commands")
    public Result<List<DeviceCommandVo>> commands(String uid, ProtocolApiType apiType) {
        List<DeviceCommandDto> commands = modelApiInvokeService.listCommands(uid, apiType);
        return success(commands.stream().map(DeviceCommandVo::of).collect(Collectors.toList()));
    }

    /**
     * 设备实时数据
     */
    @GetMapping("/realtime")
    public Result<List<RealtimeDataVo>> realtime(String uid) {
        Device device = deviceService.getByUid(uid).of().orElse(null);
        if (device == null) {
            return fail("设备不存在");
        }

        Product product = productService.detailById(device.getProductId()).of().orElse(null);
        if (product == null) {
            return fail("产品不存在");
        }

        Map<String, RealtimeData> realtimeDataMap = realtimeDataService.listOfDevice(
                product.getProtocolCode(), DeviceKey.build(device.getDeviceSn(), device.getParentDeviceSn()));

        Map<Serializable, ModelAttrMeta> attrMetaMap = modelAttrService.getAttrMetas(product.getCode());

        List<RealtimeDataVo> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(attrMetaMap)) {
            int limit = 100;
            int count = 0;
            for (ModelAttrMeta attr : attrMetaMap.values()) {
                if (count++ >= limit) {
                    break;
                }
                if (attr.getAttrType() == AttrType.W) {
                    continue; // 只写属性不展示
                }
                if (DataType.isContainer(attr.getDataType())) {
                    continue; // 容器类型不展示
                }

                RealtimeDataVo vo = new RealtimeDataVo(attr.getField(), attr.getName(), attr.getUnit());
                if (CollectionUtil.isNotEmpty(realtimeDataMap)) {
                    RealtimeData rd = realtimeDataMap.get(attr.getField());
                    if (rd != null && rd.getRealtime() != null) {
                        if (rd.getRealtime().getValue() != null) {
                            vo.setValue(rd.getRealtime().getValue().toString());
                        }

                        // 字典属性将值转为标签
                        if (CollectionUtil.isNotEmpty(attr.getDictMetas())) {
                            for (ModelAttrDictMeta dict : attr.getDictMetas()) {
                                if (dict.getDictValue().equals(vo.getValue())) {
                                    vo.setLabel(dict.getDictName());
                                    break;
                                }
                            }
                        }
                        if (rd.getRealtime().getCollectTime() != null) {
                            vo.setCollectTime(DateUtil.formatDateTime(rd.getRealtime().getCollectTime()));
                        }
                    }
                }
                list.add(vo);
            }
        }

        return success(list);
    }

    /**
     * 设备详情（含产品信息）
     */
    @GetMapping("/detail")
    public Result<DeviceDto> detail(@RequestParam String uid) {
        DeviceDto deviceDto = deviceService.getDetailByUid(uid);
        if (deviceDto == null) {
            return fail("设备不存在");
        }
        return success(deviceDto);
    }

    /**
     * 指令日志分页
     */
    @GetMapping("/cmdLog/page")
    public Result<IPage<DeviceCmdLogVo>> cmdLogPage(Page<DeviceCmdLog> page, @RequestParam String uid) {
        page.addOrder(OrderItem.desc("create_time"));
        IPage<DeviceCmdLog> result = deviceCmdLogService.lambdaQuery().eq(DeviceCmdLog::getUid, uid).page(page);

        IPage<DeviceCmdLogVo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(DeviceCmdLogVo::of)
                .collect(Collectors.toList()));

        return success(voPage);
    }

    /**
     * 产品列表（移动端筛选用）
     */
    @GetMapping("/products")
    public Result<List<ProductDto>> products() {
        return productService.listOfDetail(new Product());
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
