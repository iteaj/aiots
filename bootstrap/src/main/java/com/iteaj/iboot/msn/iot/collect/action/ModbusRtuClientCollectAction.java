package com.iteaj.iboot.msn.iot.collect.action;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Serial;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.iboot.msn.iot.rtu.ModbusRtuClientCommonProtocol;
import com.iteaj.iboot.msn.iot.service.ISerialService;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.Payload;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientCommonProtocol;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuBody;
import com.iteaj.iot.modbus.server.tcp.ModbusTcpBody;
import com.iteaj.iot.serial.SerialConnectProperties;
import com.iteaj.iot.utils.ByteUtil;

import java.util.HashMap;
import java.util.function.Consumer;

public class ModbusRtuClientCollectAction extends AbstractModbusCollectAction{

    private final ISerialService serialService;

    public ModbusRtuClientCollectAction(ISerialService serialService) {
        this.serialService = serialService;
    }

    @Override
    public void validate(CollectDetail detail) {
        super.validate(detail);
        String deviceSn = detail.getDevice().getDeviceSn();
        Serial serial = serialService.getOne(Wrappers.<Serial>lambdaQuery().eq(Serial::getCom, deviceSn))
                .ofNullable()
                .orElseThrow(() -> new CollectException("串口不存在[" + deviceSn + "]"));
        if(detail.getParams() == null) {
            detail.setParams(new HashMap<>());
            detail.getParams().put("serial", serial);
        }
    }

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

        ModbusRtuClientCommonProtocol syncProtocol = getModbusCommonProtocol(type, device.getDeviceSn(), childSn, address, signal.getNum());

        try {
            Serial serial = (Serial) detail.getParams().get("serial");
            SerialConnectProperties config = new SerialConnectProperties(serial.getCom(), serial.getBaudRate())
                    .config(serial.getDataBits(), serial.getStopBits(), serial.getParity());
            syncProtocol.sync(3000).request(config);
        } catch (ProtocolException e) {
            throw new CollectException(e.getMessage());
        }

        if(syncProtocol.getExecStatus() == ExecStatus.success) {
            ModbusRtuBody body = syncProtocol.responseMessage().getBody();
            if(body.isSuccess()) {
                Payload payload = syncProtocol.getPayload();
                Object resolveValue = resolveValue(signal.getNum(), type, address, payload);
                call.accept(resolveValue.toString());
            } else {
                throw new CollectException(body.getErrCode().getDesc());
            }
        } else {
            throw new CollectException(syncProtocol.getExecStatus().desc);
        }
    }

    @Override
    protected ModbusRtuClientCommonProtocol getModbusCommonProtocol(Integer type, String deviceSn, Integer childSn, Integer address, Integer num) {
        switch (type) {
            case IotConsts.FIELD_TYPE_BOOLEAN:
                return ModbusRtuClientCommonProtocol.buildRead01(childSn, address, 1);
            case IotConsts.FIELD_TYPE_SHORT:
                return ModbusRtuClientCommonProtocol.buildRead03(childSn, address, 1);
            case IotConsts.FIELD_TYPE_INT:
            case IotConsts.FIELD_TYPE_FLOAT:
                return ModbusRtuClientCommonProtocol.buildRead03(childSn, address, 2);
            case IotConsts.FIELD_TYPE_DOUBLE:
            case IotConsts.FIELD_TYPE_LONG:
                return ModbusRtuClientCommonProtocol.buildRead03(childSn, address, 4);
            default:
                return ModbusRtuClientCommonProtocol.buildRead03(childSn, address, num);
        }
    }

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_MODBUS_RTU;
    }

    @Override
    public String getDesc() {
        return "ModbusRtu采集器";
    }
}
