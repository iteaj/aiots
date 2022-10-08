package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.entity.Signal;

import java.util.function.Consumer;

public class PlcCollectAction extends CollectAction {

    @Override
    protected void doExec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call) {

    }
}
