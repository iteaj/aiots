package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticClientProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.omron.OmronEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.omron.OmronFuncProtocolModelApi;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractPlcProtocolSupplier<C extends NetworkConfigImpl, T extends ClientComponent, K extends IotClient>
        extends StaticClientProtocolSupplier<C, T, K> {

    private Map<String, ProtocolModelAttr> modelAttrs = new LinkedHashMap<>();
    private Map<String, AbstractProtocolModelApi> modelApis = new LinkedHashMap<>();

    public AbstractPlcProtocolSupplier() {
        // 协议模型属性
        this.modelAttrs.put(PointProtocolConfig.POINT_ADDRESS, new ProtocolModelAttr(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_NAME, DataType.t_string, AttrType.IGNORE, PlcProtocolConsts.PLC_ADDRESS_REMARK));
        this.modelAttrs.put(PointProtocolConfig.POINT_NUMBER, new ProtocolModelAttr(PointProtocolConfig.POINT_NUMBER, PlcProtocolConsts.PLC_NUMBER_NAME, DataType.t_short, AttrType.IGNORE, PlcProtocolConsts.PLC_NUMBER_REMARK));
        this.modelAttrs.put(PlcProtocolConsts.PLC_VALUE, new ProtocolModelAttr(PlcProtocolConsts.PLC_VALUE, PlcProtocolConsts.PLC_VALUE_NAME, DataType.t_any, AttrType.IGNORE, PlcProtocolConsts.PLC_VALUE_REMARK));
        this.modelAttrs.put(PlcProtocolConsts.PLC_WRITE_STATUS, new ProtocolModelAttr(PlcProtocolConsts.PLC_WRITE_STATUS, "操作状态", DataType.t_boolean, AttrType.STATUS, "操作状态"));

        // 协议模型事件接口
        this.modelApis.put("readBool", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_BOOL, "读取布尔值", "读取布尔值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的bool值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的bool值", 1)));
        this.modelApis.put("readInt16", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_INT16, "读取short值", "读取short值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取short值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的short值", 1)));
        this.modelApis.put("readUInt16", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_UINT16, "读取无符号short值", "读取无符号short值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取UShort值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的UShort值", 1)));
        this.modelApis.put("readInt32", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_INT32, "读取int值", "读取int值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的int值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的int值", 1)));
        this.modelApis.put("readUInt32", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_UINT32, "读取无符号int值", "读取无符号int值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的UInt值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的UInt值", 1)));
        this.modelApis.put("readInt64", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_INT64, "读取long值", "读取long值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的long值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的long值", 1)));
        this.modelApis.put("readFloat", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_FLOAT, "读取float值", "读取float值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的float值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的float值", 1)));
        this.modelApis.put("readDouble", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_DOUBLE, "读取double值", "读取double值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的double值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的double值", 1)));
        this.modelApis.put("readString", new OmronEventProtocolModelApi(PlcProtocolConsts.PLC_READ_STRING, "读取string值", "读取string值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_NUMBER, "读取的寄存器数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的string值", 1)));

        // 协议模型功能接口
        this.modelApis.put("writeBool", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_BOOL, "写布尔值", FuncType.W, "写布尔值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的bool值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
        this.modelApis.put("writeInt16", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_INT16, "写short值", FuncType.W, "写short值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的short值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
        this.modelApis.put("writeInt32", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_INT32, "写int值", FuncType.W, "写int值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的int值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
        this.modelApis.put("writeInt64", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_INT64, "写long值", FuncType.W, "写long值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的long值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
        this.modelApis.put("writeFloat", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_FLOAT, "写float值", FuncType.W, "写float值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的float值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
        this.modelApis.put("writeDouble", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_DOUBLE, "写double值", FuncType.W, "写double值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的double值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
        this.modelApis.put("writeString", new OmronFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_STRING, "写string值", FuncType.W, "写string值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "要写入的string值", 2))
                .addUpConfig(ProtocolModelApiConfig.upStatusBuild(PlcProtocolConsts.PLC_WRITE_STATUS, "状态")));
    }

    @Override
    public GatewayType getGatewayType() {
        return GatewayType.Gateway;
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
