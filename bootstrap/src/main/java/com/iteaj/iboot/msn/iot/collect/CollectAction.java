package com.iteaj.iboot.msn.iot.collect;

import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.Signal;

import java.util.function.Consumer;

/**
 * 采集动作
 */
public abstract class CollectAction {

    /**
     * 执行调用
     * @param taskDto 任务
     * @param signal
     * @param call
     * @param <T>
     */
    public <T> void exec(CollectTaskDto taskDto, Signal signal, Consumer<T> call) {

    }
}
