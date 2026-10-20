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
import com.iteaj.iboot.module.iot.service.IModelApiInvokeService;
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
    private final IModelApiInvokeService modelApiInvokeService;
    private final RealtimeCollectDataService collectDataService;

    public PanelsController(IDeviceService deviceService, IProductService productService, IModelApiService modelApiService, IotCacheManager cacheManager
            , CollectWarnDataService warnRecordService, RealtimeDataService realtimeDataService, IModelApiInvokeService modelApiInvokeService, RealtimeCollectDataService collectDataService) {
        this.modelApiService = modelApiService;
        this.modelApiInvokeService = modelApiInvokeService;
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
    public Result<IPage<PanelsDeviceDto>> devices(Page page, Long deviceGroupId, Long productId, String keyword) {
        DeviceDto device = new DeviceDto();
        device.setName(keyword);
        device.setProductId(productId);
        device.setDeviceGroupId(deviceGroupId);
        device.setDeviceTypes(new DeviceType[]{DeviceType.Child, DeviceType.Direct});
        PageResult<IPage<DeviceDto>> pageResult = deviceService.pageOfDetail(page, device);

        Map<Long, List<CtrlCommandDto>> productOptionsMap = new HashMap<>();
        Map<Long, ModelAttrContext> productValuesMap = new HashMap<>();

        IPage<DeviceDto> data = pageResult.getData();
        if(CollectionUtil.isNotEmpty(data.getRecords())) {
            List<Long> productIds = data.getRecords().stream()
                    .map(Device::getProductId).distinct()
                    .collect(Collectors.toList());
            List<ProductDto> list = productService.listCtrlStatusModelAttrs(productIds);
            if(CollectionUtil.isNotEmpty(list)) {
                list.forEach(productDto -> {
                    List<CtrlCommandDto> commandDtos = new ArrayList<>();
                    productDto.getFuncApis().forEach(modelApi -> {
                        modelApiService.detailByCode(modelApi.getCode()).ifPresent(ctrlModelApi -> {
                            if(ctrlModelApi != null) {
                                commandDtos.add(ctrlModelApi.resolverSimpleCommand());
                            }
                        });
                    });
                    productOptionsMap.put(productDto.getId(), commandDtos);

                    ModelAttrContext modelAttrContext = modelApiInvokeService.resolver(productDto.getAttrs());
                    productValuesMap.put(productDto.getId(), modelAttrContext);
                });

                List<PanelsDeviceDto> panelsDeviceDtos = new ArrayList<>();
                data.getRecords().forEach(deviceDto -> {
                    deviceDto.setConfig(null);
                    List<CtrlCommandDto> ctrlCommandDtos = productOptionsMap.get(deviceDto.getProductId());
                    ModelAttrContext context = productValuesMap.get(deviceDto.getProductId());

                    PanelsDeviceDto panelsDeviceDto = new PanelsDeviceDto(ctrlCommandDtos, Collections.emptyList(), deviceDto);
                    if(CollectionUtil.isNotEmpty(context.getValues())) {
                        if(CollectionUtil.isNotEmpty(ctrlCommandDtos)) {
                            for (CtrlCommandDto ctrlCommandDto : ctrlCommandDtos) {
                                if (ctrlCommandDto.getAttrField() != null) {
                                    ModelAttrRealtimeData realtimeData = realtimeDataService.getRealtimeData(deviceDto.getUid(), ctrlCommandDto.getAttrField());
                                    if(realtimeData != null && realtimeData.getRealtime() != null) {
                                        Object value = realtimeData.getRealtime().getValue();
                                        if(value != null) {
                                            panelsDeviceDto.putCtrlValue(ctrlCommandDto.getAttrField(), value.toString());
                                        }
                                    }
                                }
                            }
                        }

                        List<ModelAttrContext.ModelAttrValue> attrValues = modelApiInvokeService.conver(deviceDto.getUid(), context);
                        attrValues.forEach(attrValue -> {
                            if(panelsDeviceDto.getCollectTime() == null) {
                                panelsDeviceDto.setCollectTime(attrValue.getCollectTime());
                            } else if(attrValue.getCollectTime() != null){
                                boolean b = panelsDeviceDto.getCollectTime().getTime() > attrValue.getCollectTime().getTime();
                                panelsDeviceDto.setCollectTime(b ? panelsDeviceDto.getCollectTime() : attrValue.getCollectTime());
                            }
                        });
                        panelsDeviceDto.setValues(attrValues);
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
            productService.joinModelAttrById(item.getProductId())
                    .ifNotPresentThrow("此设备数据不完整["+item.getName()+"]")
                    .ifPresent(productDto -> {
                        debugDto.setProduct(productDto);

                        if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                            ModelAttrContext attrContext = modelApiInvokeService.resolver(productDto.getAttrs());
                            debugDto.setValueLabels(attrContext.getDictValueMap());
                            List<ModelAttrContext.ModelAttrValue> conver = modelApiInvokeService.conver(item.getUid(), attrContext);
                            debugDto.setAttrs(conver);
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
