package com.iteaj.iboot.module.iot.collect.action;

import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.collect.CollectException;
import com.iteaj.iboot.module.iot.consts.IotConsts;
import com.iteaj.iboot.module.iot.entity.CollectDetail;
import com.iteaj.iboot.module.iot.entity.Signal;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.plc.omron.OmronConnectProperties;
import com.iteaj.iot.plc.omron.OmronTcpProtocol;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

import static com.iteaj.iboot.module.iot.consts.IotConsts.*;

/**
 * 欧姆龙plc采集动作
 */
public class OmronCollectAction extends CollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_PLC_OMRON;
    }

    @Override
    public String getDesc() {
        return "欧姆龙PLC采集器";
    }

    @Override
    public void validate(Signal signal) {
        super.validate(signal);
    }

    @Override
    public void validate(CollectDetail detail) {
        super.validate(detail);
        DeviceDto device = detail.getDevice();
        if(!StringUtils.hasText(device.getIp()) || device.getPort() == null) {
            throw new CollectException("欧姆龙设备["+ device.getDeviceSn()+"]没有指定ip或者端口号");
        }
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, CollectDetail detail, Signal signal, Consumer<String> call) {
        Object value = "";
        String address = signal.getAddress();
        DeviceDto device = detail.getDevice();
        OmronTcpProtocol protocol = new OmronTcpProtocol(new OmronConnectProperties(device.getIp(), device.getPort()));
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
