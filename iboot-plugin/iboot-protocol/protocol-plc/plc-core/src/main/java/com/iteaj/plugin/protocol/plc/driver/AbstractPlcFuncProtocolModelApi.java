package com.iteaj.plugin.protocol.plc.driver;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;

import java.util.function.Consumer;

public abstract class AbstractPlcFuncProtocolModelApi extends AbstractFuncProtocolModelApi {

    public AbstractPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, TriggerMode.passive, remark);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        PlcProtocolAbstract clientProtocol = (PlcProtocolAbstract) buildProtocol(arg);
        try {
            String value = arg.getString(PlcProtocolConsts.PLC_VALUE);
            String address = arg.getString(PointProtocolConfig.POINT_ADDRESS);
            switch (this.getCode()) {
                case PlcProtocolConsts.PLC_WRITE_BYTE:
                    clientProtocol.write(address, Byte.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_BOOL:
                    clientProtocol.write(address, Boolean.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT16:
                    clientProtocol.write(address, Short.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT32:
                    clientProtocol.write(address, Integer.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT64:
                    clientProtocol.write(address, Long.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_FLOAT:
                    clientProtocol.write(address, Float.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_DOUBLE:
                    clientProtocol.write(address, Double.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_STRING:
                    clientProtocol.write(address, value);
                    break;
                case PlcProtocolConsts.PLC_WRITE_DATE:
                    clientProtocol.writeDate(address, DateUtil.parse(value, DatePattern.NORM_DATE_PATTERN));
                    break;
                case PlcProtocolConsts.PLC_WRITE_DTL_DATETIME:
                    clientProtocol.writeDltDatetime(address, DateUtil.parse(value, DatePattern.NORM_DATETIME_PATTERN));
                    break;
            }

            result.accept(InvokeResult.status(clientProtocol.getExecStatus(), clientProtocol));
        } catch (Exception e) {
            e.printStackTrace();
            result.accept(InvokeResult.fail(e.getMessage(), clientProtocol));
        }
    }
}
