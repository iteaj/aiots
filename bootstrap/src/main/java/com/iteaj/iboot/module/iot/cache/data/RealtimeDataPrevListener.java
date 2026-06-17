package com.iteaj.iboot.module.iot.cache.data;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.linkage.ConditionCalc;
import com.iteaj.iboot.module.iot.linkage.ConditionalsManager;
import com.iteaj.iboot.module.iot.service.IWarnConfigService;
import com.iteaj.iboot.module.iot.service.impl.WarnInvokeService;
import com.iteaj.iboot.module.iot.utils.IotLogger;

import java.util.List;

/**
 * 实时数据前置处理事件监听器
 */
public class RealtimeDataPrevListener implements EventGroupCollectListener, ModelAttrListener {

    private final IWarnConfigService warnConfigService;
    private final WarnInvokeService warnInvokeService;

    public RealtimeDataPrevListener(IWarnConfigService warnConfigService, WarnInvokeService warnInvokeService) {
        this.warnConfigService = warnConfigService;
        this.warnInvokeService = warnInvokeService;
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        this.supplier(info.getSupplier(), info.getDevice());
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        if(supplier != null && device != null && supplier.getValues() != null) {
            supplier.getValues().forEach(item -> handleModelaWarnAndIndicator(device, item));
        }
    }

    private void handleModelaWarnAndIndicator(RealtimeStatus device, SignalOrFieldValue item) {
        List<WarnConfig> warnConfigs = warnConfigService.listByProductIdAndModelAttrId(device.getProductId(), item.getId());
        if(CollectionUtil.isNotEmpty(warnConfigs)) {
            warnConfigs.forEach(warnConfig -> {
                if(StrUtil.isNotBlank(warnConfig.getOperator()) && StrUtil.isNotBlank(warnConfig.getThreshold())) {
                    try {
                        ConditionCalc conditionCalc = ConditionalsManager.get(warnConfig.getOperator());
                        if(conditionCalc != null) {
                            boolean calc = conditionCalc.calc(item.getValue(), warnConfig.getThreshold());
                            if(calc) {
                                item.setGrade(warnConfig.getGrade().name());
                                item.setIndicator(warnConfig.getIndicator());
                                if(warnConfig.getGrade() != WarnGrade.indicator) {
                                    warnInvokeService.sendWarn(warnConfig, device, item.getValue());
                                }
                            }
                        }
                    } catch (Exception e) {
                        IotLogger.error("属性告警", "告警或指标处理", device.getUid()
                                , "处理属性告警/指标时异常({}) 校验: {} {} {} - 错误信息: {}", warnConfig.getModelAttrField()
                                , item.getValue(), warnConfig.getOperator(), warnConfig.getThreshold(), e);
                    }
                }
            });
        }
    }

    @Override
    public int getOrder() {
        return EventGroupCollectListener.super.getOrder() - 5168;
    }
}
