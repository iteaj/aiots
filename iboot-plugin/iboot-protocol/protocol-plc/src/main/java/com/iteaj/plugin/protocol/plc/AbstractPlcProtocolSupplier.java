package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ClientProtocolSupplier;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.omron.OmronEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.omron.OmronFuncProtocolModelApi;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractPlcProtocolSupplier<C extends NetworkConfigImpl, T extends FrameworkComponent, K extends IotClient>
        extends StaticProtocolSupplier<C, T> implements ClientProtocolSupplier<C, T, K> {

    private Map<String, ProtocolModelAttr> modelAttrs = new LinkedHashMap<>();
    private Map<String, AbstractProtocolModelApi> modelApis = new LinkedHashMap<>();

    public AbstractPlcProtocolSupplier() {
        // 协议模型属性
        this.modelAttrs.put(PlcProtocolConsts.PLC_ADDRESS, new ProtocolModelAttr(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_NAME, "string", PlcProtocolConsts.PLC_ADDRESS_REMARK));
        this.modelAttrs.put(PlcProtocolConsts.PLC_NUMBER, new ProtocolModelAttr(PlcProtocolConsts.PLC_NUMBER, PlcProtocolConsts.PLC_NUMBER_NAME, "short", PlcProtocolConsts.PLC_NUMBER_REMARK));
        this.modelAttrs.put(PlcProtocolConsts.PLC_VALUE, new ProtocolModelAttr(PlcProtocolConsts.PLC_VALUE, PlcProtocolConsts.PLC_VALUE_NAME, "object", PlcProtocolConsts.PLC_VALUE_REMARK));

        // 协议模型事件接口
        this.modelApis.put("readBool", new OmronEventProtocolModelApi("readBool", "读取布尔值", "读取布尔值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的bool值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的bool值", 1)));
        this.modelApis.put("readInt16", new OmronEventProtocolModelApi("readInt16", "读取short值", "读取short值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取short值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的short值", 1)));
        this.modelApis.put("readInt32", new OmronEventProtocolModelApi("readInt32", "读取int值", "读取int值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的int值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的int值", 1)));
        this.modelApis.put("readInt64", new OmronEventProtocolModelApi("readInt64", "读取long值", "读取long值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的long值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的long值", 1)));
        this.modelApis.put("readFloat", new OmronEventProtocolModelApi("readFloat", "读取float值", "读取float值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的float值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的float值", 1)));
        this.modelApis.put("readDouble", new OmronEventProtocolModelApi("readDouble", "读取double值", "读取double值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的double值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的double值", 1)));
        this.modelApis.put("readString", new OmronEventProtocolModelApi("readString", "读取string值", "读取string值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的string值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的string值", 1)));

        // 协议模型功能接口
        this.modelApis.put("writeBool", new OmronFuncProtocolModelApi("writeBool", "写布尔值","写布尔值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的bool值", 2)));
        this.modelApis.put("writeInt16", new OmronFuncProtocolModelApi("writeInt16", "写short值", "写short值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的short值", 2)));
        this.modelApis.put("writeInt32", new OmronFuncProtocolModelApi("writeInt32", "写int值", "写int值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的int值", 2)));
        this.modelApis.put("writeInt64", new OmronFuncProtocolModelApi("writeInt64", "写long值", "写long值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的long值", 2)));
        this.modelApis.put("writeFloat", new OmronFuncProtocolModelApi("writeFloat", "写float值", "写float值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的float值", 2)));
        this.modelApis.put("writeDouble", new OmronFuncProtocolModelApi("writeDouble", "写double值", "写double值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的double值", 2)));
        this.modelApis.put("writeString", new OmronFuncProtocolModelApi("writeString", "写string值", "写string值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "要写入的string值", 2)));

    }

    public Map<String, ProtocolModelAttr> getModelAttrs() {
        return modelAttrs;
    }

    public void setModelAttrs(Map<String, ProtocolModelAttr> modelAttrs) {
        this.modelAttrs = modelAttrs;
    }

    public Map<String, AbstractProtocolModelApi> getModelApis() {
        return modelApis;
    }

    public void setModelApis(Map<String, AbstractProtocolModelApi> modelApis) {
        this.modelApis = modelApis;
    }
}
