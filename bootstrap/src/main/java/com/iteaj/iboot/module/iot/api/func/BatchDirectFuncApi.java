package com.iteaj.iboot.module.iot.api.func;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.view.ApiConfigOption;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.global.GlobalFuncApi;
import com.iteaj.framework.spi.iot.view.global.GlobalFuncApiParams;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 批量执行指令接口
 */
@Component
public class BatchDirectFuncApi implements GlobalFuncApi {

    private static final String KEY = "BDF";
    private static final String NAME = "执行指令";
    private final IotCacheManager iotCacheManager;
    private final IProductService productService;
    private final IModelApiService modelApiService;

    private final List<DataItemOption> options = Arrays.asList(
            new DataItemOption(NAME, KEY).builder()
                    .addChild("批量执行", "batch"
                            , new ApiConfigOption().addReqParams("所属组", "deviceGroupId", ""))
                    .toTop()
                    .build()
    );

    public BatchDirectFuncApi(IProductService productService, IModelApiService modelApiService, IotCacheManager iotCacheManager) {
        this.productService = productService;
        this.modelApiService = modelApiService;
        this.iotCacheManager = iotCacheManager;
    }

    @Override
    public void invoke(GlobalFuncApiParams params) {
        String apiType = params.resolveKeyItem(1);
        if(Objects.equals("batch", apiType)) {
            doInvokeDemoApi(params);
        }
    }

    private void doInvokeDemoApi(GlobalFuncApiParams params) {
        String deviceGroupId = params.getRequestParams("deviceGroupId");
        String productCode = ""; // 要调用的产品编码
        Set<String> apiCodes = new HashSet<>(); // 要执行的接口指令编码
        doInvokeApiByProduct(productCode, StrUtil.isNotEmpty(deviceGroupId) ? Long.parseLong(deviceGroupId) : null, apiCodes);
    }

    /**
     * 调用指定产品下的所有设备功能指令
     *
     * @param productCode   产品编码
     * @param deviceGroupId 所属组
     * @param apiCodes 要执行的接口
     */
    private void doInvokeApiByProduct(String productCode, Long deviceGroupId, Set<String> apiCodes) {
        Product product = this.productService.getByCode(productCode)
                .ifNotPresentThrow("未找到所属产品").getData();
        if(product.getStatus() == FuncStatus.disabled) {
            throw new ServiceException("请先启用产品["+product.getName()+"]");
        }

        Collection<RealtimeStatus> realtimeStatuses;
        if(deviceGroupId == null) {
            realtimeStatuses = iotCacheManager.listByProductCode(product.getCode());
        } else {
            realtimeStatuses = iotCacheManager.listGroupAndProduct(deviceGroupId, product.getCode());
        }

        modelApiService.detailsOfProductId(product.getId()).stream()
                .filter(item ->item.getFuncType() != FuncType.R)
                .filter(item -> apiCodes.contains(item.getCode()))
                .forEach(modelApi -> {
            if(CollectionUtil.isNotEmpty(realtimeStatuses)) {
                realtimeStatuses.forEach(realtimeStatus -> {
                    RealtimeStatus parent = null;
                    String parentDeviceSn = realtimeStatus.getParentDeviceSn();
                    if(StrUtil.isNotBlank(parentDeviceSn)) {
                        parent = iotCacheManager.get(realtimeStatus.getProtocolCode()
                                , realtimeStatus.getDeviceSn(), realtimeStatus.getParentDeviceSn());
                    }

                    if(parent != null) {
                        if(parent.getStatus() == DeviceStatus.offline) {
                            IotLogger.warn(IotLogger.NAME, "批量执行指令", realtimeStatus.getUid()
                                    , "网关设备离线({}) 设备: {} - 接口: {}/{}", parent.getDeviceName()
                                    , realtimeStatus.buildDeviceKey(), modelApi.getName(), modelApi.getCode());
                            return;
                        }
                    }

                    ProtocolModelApiInvokeParam invokeParam = ProtocolInvokeUtil.createModelApiInvokeParam(realtimeStatus, parent);
                    ProtocolInvokeUtil.invoke(modelApi, invokeParam, realtimeStatus, result -> {
                        if(result.getStatus() != ExecStatus.success) {
                            IotLogger.error(IotLogger.NAME, "批量执行指令", realtimeStatus.getUid()
                                    , "执行失败({}) 设备: {} - 接口: {}/{}", result.getReason()
                                    , realtimeStatus.buildDeviceKey(), modelApi.getName(), modelApi.getCode());
                        }
                    });
                });
            }
        });
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }
}
