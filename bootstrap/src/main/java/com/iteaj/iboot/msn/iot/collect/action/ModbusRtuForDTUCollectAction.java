package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Signal;

import java.util.function.Consumer;

/**
 * modbus rtu for dtu采集动作
 */
public class ModbusRtuForDTUCollectAction extends CollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_MODBUS_DTU_RTU;
    }

    @Override
    public String getDesc() {
        return "基于DTU的modbus rtu协议";
    }

    @Override
    public void validate(Signal signal) {
        super.validate(signal);
    }

    @Override
    public void validate(DeviceDto device) {
        super.validate(device);
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, CollectDetail detail, Signal signal, Consumer<String> call) {
        Integer type = signal.getFieldType();
        switch (type) {
            case 7:
//                ModbusRtuForDtuCommonProtocol.buildRead01(device.getDeviceSn(), ).request();
                break;
        }
    }
}
