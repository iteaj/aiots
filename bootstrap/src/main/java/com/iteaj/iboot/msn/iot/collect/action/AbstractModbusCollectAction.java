package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.Payload;
import com.iteaj.iot.server.protocol.ServerInitiativeSyncProtocol;
import com.iteaj.iot.utils.ByteUtil;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

public abstract class AbstractModbusCollectAction extends CollectAction {

    @Override
    public void validate(Signal signal) {
        super.validate(signal);
    }

    @Override
    public void validate(CollectDetail detail) {
        super.validate(detail);

        if(!StringUtils.hasText(detail.getChildSn())) {
            throw new CollectException("未配置子设备编号");
        }
    }

    protected void doExec(CollectTaskDto taskDto, CollectDetail detail, Signal signal, Consumer<String> call) {
        Integer type = signal.getFieldType();
        DeviceDto device = detail.getDevice();
        Integer childSn = Integer.valueOf(detail.getChildSn());
        Integer address = Integer.valueOf(signal.getAddress());
        ModbusCommonProtocol commonProtocol = getModbusCommonProtocol(type, device.getDeviceSn(), childSn, address, signal.getNum());

        Object value;
        ServerInitiativeSyncProtocol syncProtocol = (ServerInitiativeSyncProtocol) commonProtocol;

        try {
            syncProtocol.request();
        } catch (ProtocolException e) {
            throw new CollectException(e.getMessage());
        }

        if(syncProtocol.getExecStatus() == ExecStatus.success) {
            Payload payload = commonProtocol.getPayload();
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
            throw new CollectException(syncProtocol.getExecStatus().desc);
        }
    }

    protected abstract ModbusCommonProtocol getModbusCommonProtocol(Integer type, String deviceSn, Integer childSn, Integer address, Integer num);
}
