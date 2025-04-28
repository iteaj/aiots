package com.iteaj.iboot.plugin.script.model;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ScriptExecException;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeMethodProvide;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LinkageScriptMethodsProvider extends RealtimeMethodProvide {

    public static List<ScriptComplete> completes = Arrays.asList(
            new ScriptComplete("methods.cmd(cmdCode)", "执行指令(设备指令集指令)",1),
            new ScriptComplete("methods.cmd(cmdCode, uid)", "执行指令(产品指令集指令)",1),
            new ScriptComplete("methods.getUid(data)", "当前触发的设备",1),
            new ScriptComplete("methods.getValue(uid, field)", "获取指定字段的最新值",1),
            new ScriptComplete("methods.getPrevValue(uid, field)", "获取指定字段的上一个值",1),
            new ScriptComplete("methods.getChangeValue(uid, field)", "获取指定字段改变前的值",1),
            new ScriptComplete("methods.isUid(data, uid)", "当前触发设备是否为指定设备(uid)",2),
            new ScriptComplete("methods.isChange(uid, field)", "设备属性值是否改变(与上次值对比)",2),
            new ScriptComplete("methods.isProduct(data, productCode)", "当前触发设备是否为指定产品",2),
            new ScriptComplete("methods.isGeInterval(uid, field, 0)", "设备属性采集间隔是否>=指定值(秒)",2),
            new ScriptComplete("methods.isGeIntervalChange(uid, field, 0)", "设备属性改变值的采集间隔是否>=指定值(秒)",2),
            new ScriptComplete("methods.isProtocol(data, protocolCode)", "当前触发设备是否为指定协议",2),
            new ScriptComplete("methods.isRealtimeData(data)", "是否为实时数据触发",2),
            new ScriptComplete("methods.isRealtimeStatus(data)", "是否为状态数据触发",2),
            new ScriptComplete("console.log('')", "打印调试日志",4),
            new ScriptComplete("console.info('')", "打印信息日志",4),
            new ScriptComplete("console.warn('')", "打印告警日志",4),
            new ScriptComplete("console.error('')", "打印错误日志",4));
    public static LinkageScriptMethodsProvider INSTANCE = new LinkageScriptMethodsProvider();

    protected LinkageScriptMethodsProvider() {}

    public void cmd(String cmdCode) {
        if(StrUtil.isBlank(cmdCode)) {
            throw new ScriptExecException("指令参数必填");
        }

        IotEvenPublisher.publish(IotEventType.CmdInvoke, new CmdInvokePayload(null, cmdCode));
    }

    public void cmd(String cmdCode, String uid) {
        if(StrUtil.isBlank(cmdCode)) {
            throw new ScriptExecException("指令参数必填");
        }

        if(StrUtil.isBlank(uid)) {
            throw new ScriptExecException("设备uid必填");
        }

        IotEvenPublisher.publish(IotEventType.CmdInvoke, new CmdInvokePayload(uid, cmdCode));
    }

    public Object getValue(String uid, String field) {
        final ModelAttrRealtimeData realtimeData = getRealtimeDataService().getRealtimeData(uid, field);
        return realtimeData != null && realtimeData.getRealtime() != null ? realtimeData.getRealtime().getValue() : null;
    }

    public Object getPrevValue(String uid, String field) {
        final ModelAttrRealtimeData realtimeData = getRealtimeDataService().getRealtimeData(uid, field);
        return realtimeData != null && realtimeData.getPrev() != null ? realtimeData.getPrev().getValue() : null;
    }

    public Object getChangeValue(String uid, String field) {
        final ModelAttrRealtimeData realtimeData = getRealtimeDataService().getRealtimeData(uid, field);
        return realtimeData != null && realtimeData.getChange() != null ? realtimeData.getChange().getValue() : null;
    }

    public String getUid(Object data) {
        if(isRealtimeData(data)) {
            return getData(data).getUid();
        } else if(isRealtimeStatus(data)) {
            return getStatus(data).getUid();
        } else {
            throw new ServiceException("未知的数据类型["+data+"]");
        }
    }

    public boolean isChange(String uid, String field) {
        final ModelAttrRealtimeData realtimeData = getRealtimeDataService().getRealtimeData(uid, field);
        if(realtimeData != null) {
            final SignalOrFieldValue prev = realtimeData.getPrev();
            final SignalOrFieldValue realtime = realtimeData.getRealtime();
            if(realtime != null && prev != null) {
                return !Objects.equals(realtime.getValue(), realtime.getValue());
            } else {
                return true;
            }
        }

        return false;
    }

    public boolean isGeInterval(Object data, String field, Integer time) {
        if(this.isRealtimeData(data)) {
            final RealtimeCollectData realtime = getData(data);
            final ModelAttrRealtimeData prev = getRealtimeDataService().getRealtimeData(realtime.getUid(), field);
            if(prev == null || prev.getRealtime() == null) {
                return false;
            } else {
                final SignalOrFieldValue prevValue = prev.getRealtime();
                // 单位秒
                return TimeUnit.MILLISECONDS.toSeconds(realtime.getCollectTime().getTime()
                        - prevValue.getCollectTime().getTime()) > time;
            }
        }

        throw new ServiceException("非实时数据, 无法对比");
    }

    public boolean isGeIntervalChange(Object data, String field, Integer time) {
        if(this.isRealtimeData(data)) {
            final RealtimeCollectData realtime = getData(data);
            final ModelAttrRealtimeData prev = getRealtimeDataService().getRealtimeData(realtime.getUid(), field);
            if(prev == null || prev.getChange() == null) {
                return false;
            } else {
                final SignalOrFieldValue prevValue = prev.getChange();
                // 单位秒
                return TimeUnit.MILLISECONDS.toSeconds(realtime.getCollectTime().getTime()
                        - prevValue.getCollectTime().getTime()) > time;
            }
        }

        throw new ServiceException("非实时数据, 无法对比");
    }
}
