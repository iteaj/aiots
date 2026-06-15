package com.iteaj.iboot.module.iot.linkage.warn;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 监听告警数据并实时做持久化入库
 */
public class RealtimeWarnListener implements RealtimeInfoListener {

    private final IWarnRecordService warnRecordService;

    public RealtimeWarnListener(IWarnRecordService warnRecordService) {
        this.warnRecordService = warnRecordService;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload payload) {
        if(payload.isWarn()) {
            Collection<?> values = payload.getPayload();
            if(CollectionUtil.isNotEmpty(values)) {
                List<WarnRecord> warnRecords = values.stream()
                        .filter(Objects::nonNull)
                        .map(item -> (WarnRecord) item)
                        .toList();
                warnRecordService.saveBatch(warnRecords);
            }
        }
    }
}
