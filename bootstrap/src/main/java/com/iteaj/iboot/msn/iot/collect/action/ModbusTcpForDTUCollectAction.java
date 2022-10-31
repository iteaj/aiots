package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Signal;

import java.util.function.Consumer;

/**
 * modbus tcp for dtu采集动作
 */
public class ModbusTcpForDTUCollectAction extends CollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_MODBUS_DTU_TCP;
    }

    @Override
    public String getDesc() {
        return "基于DTU的modbus tcp协议";
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

    }
}
