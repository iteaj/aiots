package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;

import java.util.Collection;

/**
 * 将采集的数据、设备的实时状态、上报的模型属性数据发布出去, 用于给其他插件模块使用
 */
public class PluginRealtimeInfoListenerPublisher implements SignalCollectListener, EventGroupCollectListener
        , DeviceStatusListener, ModelAttrListener {

    @Override
    public void supplier(CollectDevice device, CollectData data) {

    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        DeviceStatusListener.super.supplier(devices);
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        this.supplier(info.getSupplier(), info.getDevice());
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
//        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload());
    }
}
