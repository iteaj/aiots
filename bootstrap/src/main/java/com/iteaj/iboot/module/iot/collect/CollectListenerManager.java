package com.iteaj.iboot.module.iot.collect;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CollectListenerManager {

    private List<CollectDataListener> listeners = new ArrayList<>();
    private List<SignalCollectListener> signalCollectListeners = new ArrayList<>();
    private List<EventGroupCollectListener> groupCollectListeners = new ArrayList<>();

    private static CollectListenerManager instance = new CollectListenerManager();

    protected CollectListenerManager() { }

    public static CollectListenerManager build(List<CollectDataListener> listeners) {
        if(CollectionUtil.isNotEmpty(listeners)) {
            listeners.forEach(item -> {
                instance.register(item);
            });
        }

        return instance;
    }

    public void register(CollectDataListener listener) {
        if(listener instanceof EventGroupCollectListener) {
            instance.register((EventGroupCollectListener) listener);
        } else if(listener instanceof SignalCollectListener) {
            instance.register((SignalCollectListener) listener);
        } else {
            listeners.add(listener);
        }
    }

    public void register(EventGroupCollectListener listener) {
        groupCollectListeners.add(listener);
    }

    public void register(SignalCollectListener listener) {
        signalCollectListeners.add(listener);
    }

    /**
     * 其他监听
     * @param consumer
     */
    public void forEach(Consumer<CollectDataListener> consumer) {
        this.listeners.forEach(consumer);
    }

    /**
     * 事件源采集监听
     * @param consumer
     */
    public void modelForEach(Consumer<EventGroupCollectListener> consumer) {
        groupCollectListeners.forEach(consumer);
    }

    /**
     * 点位数据采集监听
     * @param consumer
     */
    public void signalForEach(Consumer<SignalCollectListener> consumer) {
        signalCollectListeners.forEach(consumer);
    }

    public static CollectListenerManager getInstance() {
        return instance;
    }
}
