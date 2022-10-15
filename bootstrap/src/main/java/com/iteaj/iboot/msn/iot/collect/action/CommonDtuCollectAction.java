package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Signal;

import java.util.function.Consumer;

public class CommonDtuCollectAction extends CollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_COMMON_DTU;
    }

    @Override
    public String getDesc() {
        return "DTU通用协议";
    }

    @Override
    public void validate(Signal signal) {
        super.validate(signal);
    }

    @Override
    public void validate(DeviceDto deviceDto) {
        super.validate(deviceDto);
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, DeviceDto device, Signal signal, Consumer<String> call) {

    }
}
