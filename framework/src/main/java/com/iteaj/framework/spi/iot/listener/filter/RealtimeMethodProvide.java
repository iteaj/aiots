package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spring.SpringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RealtimeMethodProvide {

    private IModelAttrRealtimeDataService realtimeDataService;
    public static List<ScriptComplete> completes = Arrays.asList(
            new ScriptComplete("methods.isUid(data, '')", "是否为指定设备(uid)",2),
            new ScriptComplete("methods.isChange(data)", "值是否改变(与上次改变值对比)",2),
            new ScriptComplete("methods.isProduct(data, '')", "是否为指定产品(productCode)",2),
            new ScriptComplete("methods.isGeInterval(data, 0)", "采集间隔是否>=指定值(秒)",2),
            new ScriptComplete("methods.isGeIntervalChange(data, 0)", "改变值的采集间隔是否>=指定值(秒)",2),
            new ScriptComplete("methods.isProtocol(data, '')", "是否为指定协议(protocolCode)",2),
            new ScriptComplete("methods.isRealtimeData(data)", "是否为实时数据",2),
            new ScriptComplete("methods.isRealtimeStatus(data)", "是否为设备状态",2),
            new ScriptComplete("data.getUid()", "获取设备uid",3),
            new ScriptComplete("data.getValue()", "获取点位数据",3),
            new ScriptComplete("data.getField()", "获取点位属性",3),
            new ScriptComplete("console.log('')", "打印调试日志",4),
            new ScriptComplete("console.info('')", "打印信息日志",4),
            new ScriptComplete("console.warn('')", "打印告警日志",4),
            new ScriptComplete("console.error('')", "打印错误日志",4)
        );

    public boolean isProduct(Object data, String productCode) {
        if(this.isRealtimeData(data)) {
            return Objects.equals(getData(data).getProductCode(), productCode);
        }

        if(this.isRealtimeStatus(data)) {
            return Objects.equals(getStatus(data).getProductCode(), productCode);
        }

        return false;
    }

    public RealtimeCollectData getData(Object data) {
        return (RealtimeCollectData) data;
    }

    public RealtimeDeviceStatus getStatus(Object data) {
        return (RealtimeDeviceStatus) data;
    }

    public boolean isChange(Object data) {
        if(this.isRealtimeData(data)) {
            final RealtimeCollectData realtime = getData(data);
            final ModelAttrRealtimeData prev = getRealtimeDataService().getRealtimeData(realtime.getUid(), realtime.getField());
            if(prev == null || prev.getChange() == null) {
                return true;
            } else {
                final SignalOrFieldValue prevValue = prev.getChange();
                return !Objects.equals(prevValue.getValue(), realtime.getValue());
            }
        }

        return true;
    }

    public boolean isGeInterval(Object data, Integer time) {
        if(this.isRealtimeData(data)) {
            final RealtimeCollectData realtime = getData(data);
            final ModelAttrRealtimeData prev = getRealtimeDataService().getRealtimeData(realtime.getUid(), realtime.getField());
            if(prev == null || prev.getRealtime() == null) {
                return false;
            } else {
                final SignalOrFieldValue prevValue = prev.getRealtime();
                // 单位秒
                return TimeUnit.MILLISECONDS.toSeconds(realtime.getCollectTime().getTime()
                        - prevValue.getCollectTime().getTime()) > time;
            }
        }

        return false;
    }

    public boolean isGeIntervalChange(Object data, Integer time) {
        if(this.isRealtimeData(data)) {
            final RealtimeCollectData realtime = getData(data);
            final ModelAttrRealtimeData prev = getRealtimeDataService().getRealtimeData(realtime.getUid(), realtime.getField());
            if(prev == null || prev.getChange() == null) {
                return false;
            } else {
                final SignalOrFieldValue prevValue = prev.getChange();
                // 单位秒
                return TimeUnit.MILLISECONDS.toSeconds(realtime.getCollectTime().getTime()
                        - prevValue.getCollectTime().getTime()) > time;
            }
        }

        return false;
    }

    public boolean isRealtimeData(Object data) {
        return data instanceof RealtimeCollectData;
    }

    public boolean isRealtimeStatus(Object data) {
        return data instanceof RealtimeDeviceStatus;
    }

    public IModelAttrRealtimeDataService getRealtimeDataService() {
        if(realtimeDataService == null) {
            realtimeDataService = SpringUtils.getBean(IModelAttrRealtimeDataService.class);
        }

        return realtimeDataService;
    }
}
