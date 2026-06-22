package com.iteaj.iboot.module.iot.linkage.warn;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.consts.ProcessStatus;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
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

                if(!warnRecords.isEmpty()) {
                    List<WarnRecord> warns = warnRecords.stream()
                            .filter(item -> item.getGrade() != WarnGrade.normal)
                            .map(item -> item.setProcessStatus(ProcessStatus.UNPROCESSED.getCode()))
                            .toList();
                    if(!warns.isEmpty()) {
                        warnRecordService.saveBatch(warnRecords);
                    }

                    // 更新告警处理状态
                    warnRecords.stream().filter(item -> item.getGrade() == WarnGrade.normal)
                        .forEach(warnRecord -> {
                            warnRecordService.update(Wrappers.<WarnRecord>lambdaUpdate()
                                    .eq(WarnRecord::getUid, warnRecord.getUid())
                                    .eq(WarnRecord::getField, warnRecord.getField())
                                    .eq(WarnRecord::getProcessStatus, ProcessStatus.UNPROCESSED.getCode())
                                    .set(WarnRecord::getProcessStatus, ProcessStatus.PROCESSED.getCode())
                                    .set(WarnRecord::getProcessTime, warnRecord.getCreateTime())
                                    .set(WarnRecord::getProcessReason, "监听到解除信号, 程序自动处理"));
                    });
                }
            }
        }
    }
}
