package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDataListener;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.entity.WarnRecord;

public interface LinkageActionListener extends CollectDataListener {

    /**
     * 告警触发
     * @param config
     * @param record
     * @param status
     */
    void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status);

    /**
     * api调用
     * @param api
     * @param status
     * @param result
     */
    void apiInvoke(ModelApi api, RealtimeStatus status, InvokeResult result);
}
