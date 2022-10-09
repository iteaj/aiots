package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Device;
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
        return "使用DTU的modbus tcp协议";
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call) {

    }
}
