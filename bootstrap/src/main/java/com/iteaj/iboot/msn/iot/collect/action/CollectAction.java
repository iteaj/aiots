package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.entity.Signal;

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
     * 执行调用
     * @param taskDto 任务
     * @param signal
     * @param call
     */
    public void exec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call) throws CollectException {
        if(device == null || signal == null) {
            throw new CollectException("设备或点位不存在");
        }

        doExec(taskDto, device, signal, call);
    }

    protected abstract void doExec(CollectTaskDto taskDto, Device device, Signal signal, Consumer<String> call);
}
