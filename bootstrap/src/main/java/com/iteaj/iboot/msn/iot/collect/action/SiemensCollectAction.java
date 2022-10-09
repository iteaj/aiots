package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.plc.siemens.SiemensConnectProperties;
import com.iteaj.iot.plc.siemens.SiemensModel;
import com.iteaj.iot.plc.siemens.SiemensS7Protocol;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

import static com.iteaj.iboot.msn.iot.consts.IotConsts.*;

/**
 * 欧姆龙plc采集动作
 */
public class SiemensCollectAction extends CollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_PLC_SIEMENS;
    }

    @Override
    public String getDesc() {
        return "西门子PLC协议";
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call) {
        if(StringUtils.hasText(device.getIp()) || device.getPort() == null) {
            throw new CollectException("设备["+device.getDeviceSn()+"]没有指定ip或者端口号");
        }

        String address = signal.getAddress();
        if(!StringUtils.hasText(address)) {
            throw new CollectException("设备["+device.getDeviceSn()+"]没有指定点位["+signal.getName()+"]地址");
        }

        SiemensModel model;
        try {
            model = SiemensModel.valueOf(device.getModelName());
        } catch (Exception e) {
            throw new CollectException("西门子设备["+device.getDeviceSn()+"]型号错误["+device.getModelName()+"]");
        }

        Object value = "";
        SiemensS7Protocol protocol = new SiemensS7Protocol(new SiemensConnectProperties(device.getIp(), device.getPort(), model));
        Integer fieldType = signal.getFieldType();
        switch (fieldType) {
            case FIELD_TYPE_BYTE:
//                protocol.read(signal.getAddress())
                break;
            case FIELD_TYPE_SHORT:
                value = protocol.readInt16(address);
                break;
            case FIELD_TYPE_INT:
                value = protocol.readInt32(address);
                break;
            case FIELD_TYPE_LONG:
                value = protocol.readInt64(address);
                break;
            case FIELD_TYPE_FLOAT:
                value = protocol.readFloat(address);
                break;
            case FIELD_TYPE_DOUBLE:
                value = protocol.readDouble(address);
                break;
            case FIELD_TYPE_BOOLEAN:
                value = protocol.readBool(address);
                break;
        }

        if(protocol.getExecStatus() == ExecStatus.success) {
            call.accept(value.toString());
        } else {
            throw new CollectException(protocol.getExecStatus().desc);
        }
    }


}
