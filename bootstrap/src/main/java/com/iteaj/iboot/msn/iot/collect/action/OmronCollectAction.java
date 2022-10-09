package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.entity.Signal;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

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
        return "欧姆龙PLC协议";
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call) {
        if(StringUtils.hasText(device.getIp()) || device.getPort() == null) {
            throw new CollectException("设备["+device.getDeviceSn()+"]没有指定ip或者端口号");
        }
    }


}
