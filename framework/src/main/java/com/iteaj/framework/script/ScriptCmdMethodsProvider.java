package com.iteaj.framework.script;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ScriptCmdMethodsProvider extends ValueConvertProvide {

    public final static String NAME = "功能解析";
    public static ScriptCmdMethodsProvider INSTANCE = new ScriptCmdMethodsProvider();

    public static List<ScriptComplete> completes;

    static {
        completes = new ArrayList<>(Arrays.asList(
                new ScriptComplete("methods.writeValue(data, null)", "写值(只支持基础数据类型)",1),
                new ScriptComplete("methods.getUid(data)", "获取执行设备的uid",2),
                new ScriptComplete("methods.isUid(data, uid)", "执行设备是否为指定设备(uid)",2),
                new ScriptComplete("methods.isProduct(data, productCode)", "执行设备是否为指定产品(productCode)",2),
                new ScriptComplete("methods.isProtocol(data, protocolCode)", "执行设备是否为指定协议(protocolCode)",2),
                new ScriptComplete("methods.getInt(data, key)", "获取执行参数",3),
                new ScriptComplete("methods.getLong(data, key)", "获取执行参数",3),
                new ScriptComplete("methods.getDouble(data, key)", "获取执行参数",3),
                new ScriptComplete("methods.getBoolean(data, key)", "获取执行参数",3),
                new ScriptComplete("methods.getString(data, key)", "获取执行参数",3),
                new ScriptComplete("methods.getApiCode(data)", "获取调用的接口码",2),
                new ScriptComplete("methods.getProductCode(data)", "获取执行设备的产品码",2),
                new ScriptComplete("methods.getProtocolCode(data)", "获取执行设备的协议码",2),
                new ScriptComplete("methods.getProtocolAttr(data)", "获取此脚本绑定的协议属性",1),
                new ScriptComplete("methods.getDeviceStatus(data)", "获取执行设备的状态(online/offline)",2),
                new ScriptComplete("methods.getDeviceGroupId(data)", "获取执行设备的所属组",2),
                new ScriptComplete("methods.getDeviceSn(data)", "获取执行设备的设备编号",2),
                new ScriptComplete("methods.getParentDeviceSn(data)", "获取执行设备的父设备编号",2),
                new ScriptComplete("methods.getDeviceConfig(data, key)", "获取设备的配置项",2)
        ));
        completes.addAll(LOGGER_COMPLETES);
        completes.addAll(VALUE_CONVERT_COMPLETES);
    }

    @Override
    public String getSubType() {
        return NAME;
    }

    public boolean isUid(ProtocolModelApiInvokeParam data, String uid) {
        final RealtimeDeviceStatus device = data.getDevice();
        return Objects.equals(device.getUid(), uid);
    }

    public boolean isProduct(ProtocolModelApiInvokeParam data, String productCode) {
        final RealtimeDeviceStatus device = data.getDevice();
        return Objects.equals(device.getProductCode(), productCode);
    }

    public boolean isProtocol(ProtocolModelApiInvokeParam data, String protocolCode) {
        final RealtimeDeviceStatus device = data.getDevice();
        return Objects.equals(device.getProtocolCode(), protocolCode);
    }

    /**
     * 写值
     * @param data
     * @param value
     */
    public void writeValue(ProtocolModelApiInvokeParam data, Object value) {
        final String protocolAttrField = data.getConfig("protocolAttrField");
        if(isJSValue(value)) {
            throw new ScriptExecException("只支持写基础数据类型");
        }

        data.addParam(protocolAttrField, value);
    }

    /**
     * 获取此脚本绑定的字段
     * @param data
     * @return
     */
    public String getProtocolAttr(ProtocolModelApiInvokeParam data) {
        return data.getConfig("protocolAttrField");
    }

    /**
     * 返回调用的接口码
     * @param data
     * @return
     */
    public String getApiCode(ProtocolModelApiInvokeParam data) {
        return data.getApiCode();
    }

    /**
     * 获取设备所属产品
     * @param data
     * @return
     */
    public String getProductCode(ProtocolModelApiInvokeParam data) {
        final RealtimeDeviceStatus device = data.getDevice();
        return device == null ? null : device.getProductCode();
    }

    /**
     * 获取设备所属协议
     * @param data
     * @return
     */
    public String getProtocolCode(ProtocolModelApiInvokeParam data) {
        final RealtimeDeviceStatus device = data.getDevice();
        return device == null ? null : device.getProtocolCode();
    }

    /**
     * 获取设备在线状态
     * @param data
     * @return
     */
    public String getDeviceStatus(ProtocolModelApiInvokeParam data) {
        final RealtimeDeviceStatus device = data.getDevice();
        return device == null ? null : device.getDeviceStatus();
    }

    /**
     * 获取设备所属组
     * @param data
     * @return
     */
    public Long getDeviceGroupId(ProtocolModelApiInvokeParam data) {
        final RealtimeDeviceStatus device = data.getDevice();
        return device == null ? null : device.getDeviceGroupId();
    }

    /**
     * 获取设备编号
     * @param data
     * @return
     */
    public String getDeviceSn(ProtocolModelApiInvokeParam data) {
        return data.getDeviceSn();
    }

    /**
     * 获取父设备编号
     * @param data
     * @return
     */
    public String getParentDeviceSn(ProtocolModelApiInvokeParam data) {
        return data.getParentDeviceSn();
    }

    /**
     * 获取调用设备的uid
     * @param data
     * @return
     */
    public String getUid(ProtocolModelApiInvokeParam data) {
        return data.getUid();
    }

    /**
     * 获取指令参数
     * @param data
     * @param key
     * @return
     */
    public String getString(ProtocolModelApiInvokeParam data, String key) {
        return data.getString(key);
    }

    /**
     * 获取指令参数
     * @param data
     * @param key
     * @return
     */
    public Integer getInt(ProtocolModelApiInvokeParam data, String key) {
        return data.getInteger(key);
    }

    /**
     * 获取指令参数
     * @param data
     * @param key
     * @return
     */
    public Double getDouble(ProtocolModelApiInvokeParam data, String key) {
        return data.getDouble(key);
    }

    /**
     * 获取指令参数
     * @param data
     * @param key
     * @return
     */
    public Boolean getBoolean(ProtocolModelApiInvokeParam data, String key) {
        return data.getBoolean(key);
    }

    /**
     * 获取调用设备的配置项
     * @param data
     * @param key
     * @return
     */
    public String getDeviceConfig(ProtocolModelApiInvokeParam data, String key) {
        return data.getDeviceConfig(key);
    }

    /**
     * 获取调用设备父设备的配置项
     * @param data
     * @param key
     * @return
     */
    public String getParentConfig(ProtocolModelApiInvokeParam data, String key) {
        return data.getParentDeviceConfig(key);
    }
}
