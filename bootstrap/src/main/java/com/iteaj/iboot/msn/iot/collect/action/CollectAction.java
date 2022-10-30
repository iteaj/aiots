package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.entity.Signal;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

/**
 * 采集动作
 */
public abstract class CollectAction {

    /**
     * 采集动作名称
     * @return
     */
    public abstract String getName();

    /**
     * 采集动作说明
     * @return
     */
    public abstract String getDesc();

    /**
     * 点位数据校验
     * @param signal
     */
    public void validate(Signal signal) throws CollectException {
        String address = signal.getAddress();
        if(!StringUtils.hasText(address)) {
            throw new CollectException("点位["+signal.getName()+"]没有指定地址");
        }
    }

    /**
     * 设备数据校验
     * @param device
     */
    public void validate(DeviceDto device) throws CollectException {
        if(device == null) {
            throw new CollectException("参数[device]不能为null");
        }

        if(!StringUtils.hasText(device.getDeviceSn())) {
            throw new CollectException("未指定设备编号");
        }
    }

    /**
     * 执行调用
     * @param taskDto 任务
     * @param signal
     * @param call
     */
    public void exec(CollectTaskDto taskDto, DeviceDto device, Signal signal, Consumer<String> call) throws CollectException {
        doExec(taskDto, device, signal, call);
    }

    protected abstract void doExec(CollectTaskDto taskDto, DeviceDto device, Signal signal, Consumer<String> call);
}
