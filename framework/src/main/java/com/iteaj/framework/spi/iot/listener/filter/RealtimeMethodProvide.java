package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.script.JavaScriptValueProvide;
import com.iteaj.framework.script.LoggerMethodProvide;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ScriptExecException;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spring.SpringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RealtimeMethodProvide implements JavaScriptValueProvide, LoggerMethodProvide {

    public static List<ScriptComplete> completes;
    public final static String NAME = "数据过滤";
    private IModelAttrRealtimeDataService realtimeDataService;
    public static RealtimeMethodProvide INSTANCE = new RealtimeMethodProvide();

    static {
        completes = new ArrayList<>(Arrays.asList(
                new ScriptComplete("methods.isUid(data, '')", "是否为指定设备(uid)",2),
                new ScriptComplete("methods.isChange(data)", "值是否改变(与上次改变值对比)",2),
                new ScriptComplete("methods.isProduct(data, '')", "是否为指定产品(productCode)",2),
                new ScriptComplete("methods.isGeInterval(data, 0)", "入库间隔是否>=指定值(秒)",2),
                new ScriptComplete("methods.isProtocol(data, '')", "是否为指定协议(protocolCode)",2),
                new ScriptComplete("methods.isRealtimeData(data)", "是否为实时数据",2),
                new ScriptComplete("methods.isRealtimeStatus(data)", "是否为状态数据",2),
                new ScriptComplete("data.getUid()", "获取设备uid",3),
                new ScriptComplete("data.getValue()", "获取点位数据",3),
                new ScriptComplete("data.getField()", "获取点位属性",3)
        ));

        completes.addAll(LOGGER_COMPLETES);
    }

    protected RealtimeMethodProvide() {}

    public boolean isUid(Object data, String uid) {
        if(this.isRealtimeData(data)) {
            return Objects.equals(getData(data).getUid(), uid);
        }

        if(this.isRealtimeStatus(data)) {
            return Objects.equals(getStatus(data).getUid(), uid);
        }

        throw new ScriptExecException("错误的数据类型[" + data.getClass().getSimpleName() + "]");
    }

    public boolean isProduct(Object data, String productCode) {
        if(this.isRealtimeData(data)) {
            return Objects.equals(getData(data).getProductCode(), productCode);
        }

        if(this.isRealtimeStatus(data)) {
            return Objects.equals(getStatus(data).getProductCode(), productCode);
        }

        throw new ScriptExecException("错误的数据类型[" + data.getClass().getSimpleName() + "]");
    }

    public boolean isProtocol(Object data, String protocolCode) {
        if(this.isRealtimeData(data)) {
            return Objects.equals(getData(data).getProtocolCode(), protocolCode);
        } else if(this.isRealtimeStatus(data)) {
            return Objects.equals(getStatus(data).getProtocolCode(), protocolCode);
        }

        throw new ScriptExecException("错误的数据类型[" + data.getClass().getSimpleName() + "]");
    }

    public RealtimeCollectData getData(Object data) {
        return (RealtimeCollectData) data;
    }

    public RealtimeDeviceStatus getStatus(Object data) {
        return (RealtimeDeviceStatus) data;
    }

    public boolean isChange(Object data) {
        if(this.isRealtimeData(data)) {
            RealtimeCollectData realtimeData = getData(data);
            final ModelAttrRealtimeData cache = getRealtimeDataService().getRealtimeData(realtimeData.getUid(), realtimeData.getField());
            if(cache != null && Boolean.TRUE.equals(realtimeData.getStatus())) {
                final SignalOrFieldValue prev = cache.getPrev();
                if(prev != null) {
                    return !Objects.equals(prev.getValue(), realtimeData.getValue());
                } else {
                    return true;
                }
            }

            return false;
        }

        throw new ServiceException("非实时数据, 无法对比");
    }

    public boolean isGeInterval(Object data, Integer time) {
        if(this.isRealtimeData(data)) {
            final RealtimeCollectData realtime = getData(data);
            final ModelAttrRealtimeData cache = getRealtimeDataService().getRealtimeData(realtime.getUid(), realtime.getField());
            if (cache != null && cache.getRealtime() != null) {
                RealtimeDataExtra extra = cache.getExtra();
                if (extra.getGeStartTime() == 0) { // 第一次入库
                    extra.setGeStartTime(System.currentTimeMillis());
                    return true; // 第一次入库，直接返回
                }

                // 距离上一次入库时间已经大于指定时间
                long currentTimeMillis = System.currentTimeMillis();
                if (TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis - extra.getGeStartTime()) >= time) {
                    extra.setGeStartTime(currentTimeMillis); // 重置当前入库时间
                    return true;
                }
            }

            return false;
        }

        throw new ServiceException("非实时数据, 无法对比");
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

    @Override
    public String getSubType() {
        return NAME;
    }
}
