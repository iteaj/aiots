package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Device;
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
    protected void doExec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call) {

    }
}
