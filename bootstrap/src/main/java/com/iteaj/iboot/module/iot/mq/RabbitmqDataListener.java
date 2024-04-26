package com.iteaj.iboot.module.iot.mq;

import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitmqDataListener implements SignalCollectListener, EventGroupCollectListener, LinkageActionListener {

    @Override
    public void supplier(CollectDevice device, CollectData data) {

    }

    @Override
    public void finished(CollectDevice device, List<CollectData> data) {
        SignalCollectListener.super.finished(device, data);
    }

    @Override
    public void finished(CollectInfo info) {
        EventGroupCollectListener.super.finished(info);
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {

    }

    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
        LinkageActionListener.super.warnTrigger(config, record, status);
    }
}
