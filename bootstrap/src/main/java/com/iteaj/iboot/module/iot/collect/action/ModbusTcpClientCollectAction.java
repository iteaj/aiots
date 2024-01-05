package com.iteaj.iboot.module.iot.collect.action;

import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.collect.CollectException;
import com.iteaj.iboot.module.iot.consts.IotConsts;
import com.iteaj.iboot.module.iot.entity.CollectDetail;
import com.iteaj.iboot.module.iot.entity.Signal;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.Payload;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientCommonProtocol;
import com.iteaj.iot.modbus.server.tcp.ModbusTcpBody;
import com.iteaj.iot.utils.ByteUtil;

import java.util.function.Consumer;

public class ModbusTcpClientCollectAction extends AbstractModbusCollectAction{

    @Override
    protected void doExec(CollectTaskDto taskDto, CollectDetail detail, Signal signal, Consumer<String> call) {
        Integer type = signal.getFieldType();
        DeviceDto device = detail.getDevice();
        Integer childSn = Integer.valueOf(detail.getChildSn());
        Integer address = Integer.valueOf(signal.getAddress());

        ModbusTcpClientCommonProtocol syncProtocol = (ModbusTcpClientCommonProtocol)getModbusCommonProtocol(type, device.getDeviceSn(), childSn, address, signal.getNum());

        Object value;
        try {
            syncProtocol.sync(3000).request(new ClientConnectProperties(device.getIp(), device.getPort(), device.getDeviceSn()));
        } catch (ProtocolException e) {
            throw new CollectException(e.getMessage());
        }

        if(syncProtocol.getExecStatus() == ExecStatus.success) {
            ModbusTcpBody body = syncProtocol.responseMessage().getBody();
            if(body.isSuccess()) {
                Payload payload = syncProtocol.getPayload(detail.getDataFormat());
                value = resolveValue(signal, type, address, payload);

                call.accept(value.toString());
            } else {
                throw new CollectException(body.getErrCode().getDesc());
            }
        } else {
            throw new CollectException(syncProtocol.getExecStatus().desc);
        }
    }

    @Override
    protected ModbusCommonProtocol getModbusCommonProtocol(Integer type, String deviceSn, Integer childSn, Integer address, Integer num) {
        switch (type) {
            case IotConsts.FIELD_TYPE_BOOLEAN:
                return ModbusTcpClientCommonProtocol.buildRead01(childSn, address, 1);
            case IotConsts.FIELD_TYPE_SHORT:
                return ModbusTcpClientCommonProtocol.buildRead03(childSn, address, 1);
            case IotConsts.FIELD_TYPE_INT:
            case IotConsts.FIELD_TYPE_FLOAT:
                return ModbusTcpClientCommonProtocol.buildRead03(childSn, address, 2);
            case IotConsts.FIELD_TYPE_DOUBLE:
            case IotConsts.FIELD_TYPE_LONG:
                return ModbusTcpClientCommonProtocol.buildRead03(childSn, address, 4);
            default:
                return ModbusTcpClientCommonProtocol.buildRead03(childSn, address, num);
        }
    }

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_MODBUS_TCP;
    }

    @Override
    public String getDesc() {
        return "ModbusTcp采集器";
    }
}
