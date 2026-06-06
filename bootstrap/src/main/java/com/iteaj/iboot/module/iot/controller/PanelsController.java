package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.data.CollectWarnDataService;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.framework.spi.iot.data.RealtimeDataParam;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.model.DeviceStatusModelApiManager;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 运行展板模块
 */
@RestController
@RequestMapping("/iot/panels")
public class PanelsController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IModelApiService modelApiService;
    private final IotCacheManager cacheManager;
    private final CollectWarnDataService warnRecordService;
    private final RealtimeDataService realtimeDataService;
    private final RealtimeCollectDataService collectDataService;

    public PanelsController(IDeviceService deviceService, IProductService productService, IModelApiService modelApiService, IotCacheManager cacheManager
            , CollectWarnDataService warnRecordService, RealtimeDataService realtimeDataService, RealtimeCollectDataService collectDataService) {
        this.modelApiService = modelApiService;
        this.collectDataService = collectDataService;
        this.deviceService = deviceService;
        this.productService = productService;
        this.cacheManager = cacheManager;
        this.warnRecordService = warnRecordService;
        this.realtimeDataService = realtimeDataService;
    }

    /**
     * 设备列表
     * @param deviceGroupId 设备分组
     * @return
     */
    @GetMapping("/devices")
    public Result<IPage<PanelsDeviceDto>> devices(Page page, Long deviceGroupId, Long productId) {
        DeviceDto device = new DeviceDto();
        device.setProductId(productId);
        device.setDeviceGroupId(deviceGroupId);
        device.setDeviceTypes(new DeviceType[]{DeviceType.Child, DeviceType.Direct});
        PageResult<IPage<DeviceDto>> pageResult = deviceService.pageOfDetail(page, device);

        Map<Long, String> productProtocolMap = new HashMap<>();
        Map<Long, CtrlCommandDto> productOptionsMap = new HashMap<>();
        Map<Long, Map<String, String>> productAttrDictsMap = new HashMap<>();
        Map<Long, List<PanelsDeviceDto.AttrValue>> productValuesMap = new HashMap<>();

        IPage<DeviceDto> data = pageResult.getData();
        if(CollectionUtil.isNotEmpty(data.getRecords())) {
            List<Long> productIds = data.getRecords().stream()
                    .map(Device::getProductId).distinct()
                    .collect(Collectors.toList());
            List<ProductDto> list = productService.listCtrlStatusModelAttrs(productIds);
            if(CollectionUtil.isNotEmpty(list)) {
                list.forEach(productDto -> {
                    List<PanelsDeviceDto.AttrValue> values = new ArrayList<>();
                    productProtocolMap.put(productDto.getId(), productDto.getProtocolCode());
                    ModelApi ctrlModelApi = modelApiService.getCtrlStatusByProductId(productDto.getId());
                    if(ctrlModelApi != null) {
                        CtrlCommandDto ctrlCommandDto = ctrlModelApi.resolverSimpleCommand();
                        productOptionsMap.put(productDto.getId(), ctrlCommandDto);
                    }

                    if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                        Map<String, String> attrDictMap = new HashMap<>();
                        productDto.getAttrs().forEach(attr -> {
                            if(attr.getAttrType() != AttrType.W) { // 读和读写
                                if(!DataType.isContainer(attr.getDataType())) {
                                    values.add(new PanelsDeviceDto.AttrValue(attr.getField(), attr.getName(), attr.getUnit(), attr.getCtrlStatus()));
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

                List<PanelsDeviceDto> panelsDeviceDtos = new ArrayList<>();
                data.getRecords().forEach(deviceDto -> {
                    deviceDto.setConfig(null);
                    CtrlCommandDto ctrlCommandDto = productOptionsMap.get(deviceDto.getProductId());
                    Map<String, String> dictStringMap = productAttrDictsMap.get(deviceDto.getProductId());
                    List<PanelsDeviceDto.AttrValue> attrValues = productValuesMap.get(deviceDto.getProductId());

                    PanelsDeviceDto panelsDeviceDto = new PanelsDeviceDto(ctrlCommandDto, Collections.EMPTY_LIST, deviceDto);
//                    // 获取每个属性的实时数据
                    if(CollectionUtil.isNotEmpty(attrValues)) {
                        if(ctrlCommandDto != null) {
                            ModelAttrRealtimeData realtimeData = realtimeDataService.getRealtimeData(deviceDto.getUid(), ctrlCommandDto.getAttrField());
                            if(realtimeData != null && realtimeData.getRealtime() != null) {
                                panelsDeviceDto.setCtrlValue(realtimeData.getRealtime().getValue().toString());
                            }
                        }

                        List<PanelsDeviceDto.AttrValue> copyAttrValues = attrValues.stream()
                                .map(item -> new PanelsDeviceDto.AttrValue(item.getField(), item.getName(), item.getUnit(), item.getCtrlStatus()))
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

                                    String value = realtimeData.getRealtime().getValue().toString();
                                    attrValue.setValue(value);
                                    panelsDeviceDto.setCollectTime(realtimeData.getRealtime().getCollectTime());

                                    // 将采集到的值转成字典label
                                    attrValue.setLabel(dictStringMap.get(attrValue.getField() + "_" + value));
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
     * 调试详情
     * @param uid
     * @return
     */
    @GetMapping("detail")
    public Result<PanelsDetailDto> detail(String uid) {
        PanelsDetailDto debugDto = new PanelsDetailDto();
        deviceService.getByUid(uid)
                .ifNotPresentThrow("设备不存在").of().ifPresent(item -> {
            debugDto.setDevice(item);
            productService.joinDetailById(item.getProductId())
                    .ifNotPresentThrow("此设备数据不完整["+item.getName()+"]")
                    .ifPresent(productDto -> {
                        debugDto.setProduct(productDto);
                        if(!CollectionUtils.isEmpty(productDto.getFuncApis())) {
                            // 过滤出被动的功能接口
                            List<ModelApi> modelApis = productDto.getFuncApis().stream()
                                    .filter(api -> api.getTriggerMode() == TriggerMode.passive && !Boolean.TRUE.equals(api.getAsStatus()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(modelApis)) {
                                modelApis.forEach(api -> debugDto.addApiDebugParam(api.getCode(), api.resolveDownApiConfig(productDto.getAttrs())));
                            }

                            productDto.setFuncApis(modelApis);
                        }

                        if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
                            // 过滤出被动的事件接口
                            List<ModelApi> modelApis = productDto.getEventApis().stream()
                                    .filter(api -> api.getTriggerMode() == TriggerMode.passive)
                                    .collect(Collectors.toList());
                            productDto.setEventApis(modelApis);
                        }
                        Map<String, RealtimeData> realtimeDataMap = realtimeDataService.listOfDevice(productDto.getProtocolCode()
                                , DeviceKey.build(item.getDeviceSn(), item.getParentDeviceSn()));
                        if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                            productDto.getAttrs().stream().filter(attr -> (attr.getAttrType() != AttrType.W
                                    && !DataType.isContainer(attr.getDataType())))
                                .forEach(attr -> {
                                    if(realtimeDataMap != null) {
                                        RealtimeData realtimeData = realtimeDataMap.get(attr.getField());
                                        if(realtimeData != null && realtimeData.getRealtime() != null) {
                                            SignalOrFieldValue realtime = realtimeData.getRealtime();
                                            debugDto.addAttr(attr.getId(), attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType()
                                                    , realtime.getValue(), realtime.getCollectTime(), realtime.getStatus());
                                            return;
                                        }
                                    }

                                    debugDto.addAttr(attr.getId(), attr.getField(), attr.getName(), attr.getUnit(), attr.getAttrType());
                            });
                        }

                        productDto.setAttrs(null);
                    });
        });

        return success(debugDto);
    }

    /**
     * 切换控制状态
     * @param dto id为设备的uid值
     * @return
     */
    @PostMapping("switchCtrlStatus")
    public Result switchCtrlStatus(@RequestBody StatusSwitchDto dto) {
        this.deviceService.getByUid(dto.getId().toString())
                .ifNotPresentThrow("设备不存在")
                .ifPresent(item -> {
            this.productService.getCtrlStatusModelApi(item.getProductId())
                    .ifNotPresentThrow("此设备绑定的产品有误").ifPresent(productDto -> {
               if(CollectionUtil.isEmpty(productDto.getFuncApis())) {
                   throw new ServiceException("此产品["+productDto.getName()+"]没有指定状态接口");
               }

               if(productDto.getStatus() == FuncStatus.disabled) {
                   throw new ServiceException("请先启用产品["+productDto.getName()+"]");
               }

                ModelApi modelApi = productDto.getFuncApis().get(0);
                DeviceKey deviceKey = DeviceKey.build(item.getDeviceSn(), item.getParentDeviceSn());
                RealtimeStatus realtimeStatus = cacheManager.get(productDto.getProtocolCode(), deviceKey);

                if(CollectionUtil.isEmpty(productDto.getAttrs())) {
                    throw new ServiceException("此产品["+productDto.getName()+"]没有模型属性");
                }

                ModelAttr ctrlStatusAttr = productDto.getAttrs().stream()
                        .filter(attr -> Boolean.TRUE.equals(attr.getCtrlStatus()))
                        .findFirst().orElse(null);
                if(ctrlStatusAttr == null) {
                    throw new ServiceException("此产品["+productDto.getName()+"]没有设置控制属性");
                }

                JSONObject jsonObject = new JSONObject();
                ProtocolModelApiInvokeParam invokeParam = new ProtocolModelApiInvokeParam(realtimeStatus.getUid(), jsonObject);
                if(CollectionUtil.isNotEmpty(modelApi.getDownConfig())) {
                    modelApi.getDownConfig().forEach(config -> {
                        if(config.getModelAttrId() != null) {
                            // 控制属性
                            if(ctrlStatusAttr.getField().equals(config.getAttrField())) {
                                jsonObject.put(config.getProtocolAttrField(), dto.getStatus());
                            } else {
                                jsonObject.put(config.getProtocolAttrField(), config.getAttrDefaultValue());
                            }
                        } else {
                            jsonObject.put(config.getProtocolAttrField(), config.getValue());
                        }
                    });
                }

                ProtocolInvokeUtil.invoke(modelApi, invokeParam, realtimeStatus, (result) -> {
                    if(result.getStatus() != ExecStatus.success) {
                        throw new ServiceException("调用设备失败["+result.getReason()+"]");
                    } else {
                        // 将实时值更新为下发的值
                        realtimeDataService.put(realtimeStatus.getProtocolCode(), deviceKey
                                , SignalOrFieldValue.build(ctrlStatusAttr.getId(), ctrlStatusAttr.getField(), dto.getStatus()));
                    }
                });
            });
        });

        return success();
    }

    /**
     * 告警列表
     * @param page
     * @param entity
     * @return
     */
    @GetMapping("/warn")
    public Result<IPage<WarnRecord>> list(Page<WarnRecord> page, WarnRecord entity) {
        return (Result<IPage<WarnRecord>>) this.warnRecordService.detailPage(page, entity);
    }

    /**
     * 模型属性历史数据
     * @param page
     * @param entity
     * @return
     */
    @GetMapping("/data")
    public Result<IPage<CollectDataDto>> list(Page<CollectData> page, RealtimeDataParam entity) {
        return this.collectDataService.detailOfPage(page, entity);
    }
}
