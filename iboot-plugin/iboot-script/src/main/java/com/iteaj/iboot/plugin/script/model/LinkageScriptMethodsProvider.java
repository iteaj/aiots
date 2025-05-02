package com.iteaj.iboot.plugin.script.model;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ScriptExecException;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.iot.listener.ScriptInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeMethodProvide;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LinkageScriptMethodsProvider extends RealtimeMethodProvide {

    private final static String Split = ":";
    private Map<String, Long> interval = new ConcurrentHashMap<>();

    public static List<ScriptComplete> completes = Arrays.asList(
            new ScriptComplete("methods.invoke(cmd)", "执行指令(设备指令集指令)",1),
            new ScriptComplete("methods.invoke(cmd, uid)", "执行指令(产品指令集指令)",1),
            new ScriptComplete("methods.warn(warnMsgId, uid, field, value, content)", "发送告警",1),
            new ScriptComplete("methods.statusWarn(msgTempId, uid, field, value, content)", "发送状态告警",1),
            new ScriptComplete("methods.fatalWarn(msgTempId, uid, field, value, content)", "发送严重告警",1),
            new ScriptComplete("methods.urgentWarn(msgTempId, uid, field, value, content)", "发送紧急告警",1),
            new ScriptComplete("methods.noticeWarn(msgTempId, uid, field, value, content)", "发送告警通知",1),
            new ScriptComplete("methods.normalWarn(msgTempId, uid, field, value, content)", "发送告警解除",1),
            new ScriptComplete("methods.isGtPrevInterval(uid, field, time)", "距离上一次告警时间是否大于指定时间(秒)",1),
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

    public void invoke(String cmdCode) {
        if(StrUtil.isBlank(cmdCode)) {
            throw new ScriptExecException("指令参数必填");
        }

        IotEvenPublisher.publish(IotEventType.ScriptInvoke, ScriptInvokePayload.build(null, cmdCode));
    }

    public void invoke(String cmdCode, String uid) {
        if(StrUtil.isBlank(cmdCode)) {
            throw new ScriptExecException("指令参数必填");
        }

        if(StrUtil.isBlank(uid)) {
            throw new ScriptExecException("设备uid必填");
        }

        IotEvenPublisher.publish(IotEventType.ScriptInvoke, ScriptInvokePayload.build(uid, cmdCode));
    }

    public void warn(Long warnMsgId, String uid, String field, String value, String content) {
        this.doWarn(warnMsgId, uid, field, value, "告警", content);
    }

    public void fatalWarn(Long warnMsgId, String uid, String field, String value, String content) {
        this.doWarn(warnMsgId, uid, field, value, "严重", content);
    }

    public void urgentWarn(Long warnMsgId, String uid, String field, String value, String content) {
        this.doWarn(warnMsgId, uid, field, value, "紧急", content);
    }

    public void noticeWarn(Long warnMsgId, String uid, String field, String value, String content) {
        this.doWarn(warnMsgId, uid, field, value, "通知", content);
    }

    public void normalWarn(Long warnMsgId, String uid, String field, String value, String content) {
        this.doWarn(warnMsgId, uid, field, value, "解除", content);
    }

    public void statusWarn(Long warnMsgId, String uid, String field, String value, String content) {
        this.doWarn(warnMsgId, uid, field, value, "状态", content);
    }

    /**
     * 距离上一次告警时间是否大于指定时间
     * @param uid 设备uid
     * @param field 模型属性
     * @param time 时间(秒)
     * @return
     */
    public boolean isWarnGtPrevInterval(String uid, String field, Integer time) {
        if(StrUtil.isBlank(uid) || StrUtil.isBlank(field) || time == null || time <= 0) {
            throw new ScriptExecException("函数[isWarnGtPrevInterval]入参错误");
        }

        final Long warnTime = interval.get(uid + Split + field);
        if(warnTime == null) {
            return true;
        }

        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - warnTime) > time;
    }

    public void doWarn(Long warnMsgId, String uid, String field, String value, Object grade, String content) {
        if(warnMsgId == null || StrUtil.isBlank(uid) || StrUtil.isBlank(field)) {
            throw new ScriptExecException("发送告警["+grade+"]入参错误");
        }

        final RealtimeDeviceStatus device = getRealtimeDataService().getDeviceStatus(uid);
        if(device == null) {
            throw new ScriptExecException("告警失败 设备不存在["+uid+"]");
        }

        content = content != null ? content : String.format("设备[%s]触发[%s]告警, 告警值[%s=%s]"
                , device.getDeviceName(), grade, field, value);

        final Warn warn = new Warn(device).setField(field).setValue(value)
                .setContent(content).setWarnMsgId(warnMsgId).setGrade(grade);


        final String key = uid + Split + field;
        if(Objects.equals("解除", grade)) {
            if(interval.containsKey(key)) {
                interval.remove(key);
            } else {
                return; // 之前没有发送过告警, 不发送解除告警
            }
        } else if(!Objects.equals("状态", grade)) {
            interval.put(key, System.currentTimeMillis());
        }

        // 以脚本方式触发的告警
        IotEvenPublisher.publish(IotEventType.ScriptInvoke, ScriptInvokePayload.build(warn));
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

    @Setter
    @Accessors(chain = true)
    public class Warn implements WarnData {

        private String value;
        private String field;
        private String content;
        private Date warnTime;
        private Object grade;
        /**
         * 告警模板id
         */
        private Long warnMsgId;
        private RealtimeDeviceStatus device;

        public Warn(RealtimeDeviceStatus device) {
            this.device = device;
            this.warnTime = new Date();
        }

        @Override
        public String getUid() {
            return device.getUid();
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public String getField() {
            return this.field;
        }

        @Override
        public String getDeviceSn() {
            return device.getDeviceSn();
        }

        @Override
        public String getContent() {
            return this.content;
        }

        @Override
        public Date getWarnTime() {
            return this.warnTime;
        }

        @Override
        public Object getGrade() {
            return this.grade;
        }

        @Override
        public Long getWarnConfigId() {
            return this.warnMsgId;
        }

        @Override
        public String getProductCode() {
            return device.getProductCode();
        }

        @Override
        public String getDeviceName() {
            return device.getDeviceName();
        }

    }
}
