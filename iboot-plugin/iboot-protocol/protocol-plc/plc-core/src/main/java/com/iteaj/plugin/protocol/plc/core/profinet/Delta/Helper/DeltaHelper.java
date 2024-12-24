package com.iteaj.plugin.protocol.plc.core.profinet.Delta.Helper;

import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.Delta.IDelta;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.types.FunctionOperateExTwo;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 台达PLC的相关的帮助类，公共的地址解析的方法。<br />
 * Delta PLC related help classes, public address resolution methods.
 */
public class DeltaHelper {

    public static OperateResultExOne<String> TranslateToModbusAddress(IDelta delta, String address, byte modbusCode ) {
        switch (delta.GetSeries()) {
            case Dvp:
                return DeltaDvpHelper.ParseDeltaDvpAddress(address, modbusCode);
            case AS:
                return DeltaASHelper.ParseDeltaASAddress(address, modbusCode);
            default:
                return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        }
    }

    public static OperateResultExOne<boolean[]> ReadBool(IDelta delta, FunctionOperateExTwo<String,Short, OperateResultExOne<boolean[]>> readBoolFunc, String address, short length ) {
        switch (delta.GetSeries()) {
            case Dvp:
                return DeltaDvpHelper.ReadBool(readBoolFunc, address, length);
            case AS:
                return readBoolFunc.Action(address, length);
            default:
                return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
        }
    }

    public static OperateResult Write(IDelta delta, FunctionOperateExTwo<String, boolean[], OperateResult> writeBoolFunc, String address, boolean[] values ) {
        switch (delta.GetSeries()) {
            case Dvp:
                return DeltaDvpHelper.Write(writeBoolFunc, address, values);
            case AS:
                return writeBoolFunc.Action(address, values);
            default:
                return new OperateResult(StringResources.Language.NotSupportedDataType());
        }
    }

    public static OperateResultExOne<byte[]> Read( IDelta delta, FunctionOperateExTwo<String,Short, OperateResultExOne<byte[]>> readFunc, String address, short length ) {
        switch (delta.GetSeries()) {
            case Dvp:
                return DeltaDvpHelper.Read(readFunc, address, length);
            case AS:
                return readFunc.Action(address, length);
            default:
                return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }
    }

    public static OperateResult Write(IDelta delta, FunctionOperateExTwo<String, byte[], OperateResult> writeFunc, String address, byte[] value ) {
        switch (delta.GetSeries()) {
            case Dvp:
                return DeltaDvpHelper.Write(writeFunc, address, value);
            case AS:
                return writeFunc.Action(address, value);
            default:
                return new OperateResult(StringResources.Language.NotSupportedDataType());
        }
    }

}
