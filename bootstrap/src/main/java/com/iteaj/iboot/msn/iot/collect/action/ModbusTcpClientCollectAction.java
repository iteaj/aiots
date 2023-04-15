package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Signal;
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

        if(address > 40001) {
            address = address - 40001;
        }

        if(type == IotConsts.FIELD_TYPE_BOOLEAN) {
            address = address - 1;
        }

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
                Payload payload = syncProtocol.getPayload();
                switch (type) {
                    case IotConsts.FIELD_TYPE_BOOLEAN:
                        value = payload.readBoolean(address); break;
                    case IotConsts.FIELD_TYPE_SHORT:
                        value = payload.readShort(address); break;
                    case IotConsts.FIELD_TYPE_INT:
                        value = payload.readInt(address); break;
                    case IotConsts.FIELD_TYPE_FLOAT:
                        value = payload.readFloat(address); break;
                    case IotConsts.FIELD_TYPE_DOUBLE:
                        value = payload.readDouble(address); break;
                    case IotConsts.FIELD_TYPE_LONG:
                        value = payload.readLong(address); break;
                    case IotConsts.FIELD_TYPE_STRING:
                        value = payload.readString(address, signal.getNum()); break;
                    default:
                        value = ByteUtil.bytesToHex(payload.getPayload()); break;
                }

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
