package com.iteaj.iboot.module.iot.collect;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CollectListenerManager {

    private List<SignalCollectListener> signalCollectListeners = new ArrayList<>();
    private List<EventGroupCollectListener> groupCollectListeners = new ArrayList<>();

    private static CollectListenerManager instance = new CollectListenerManager();

    protected CollectListenerManager() { }

    public static CollectListenerManager build(List<CollectDataListener> listeners) {
        if(CollectionUtil.isNotEmpty(listeners)) {
            listeners.forEach(item -> {
                if(item instanceof EventGroupCollectListener) {
                    instance.register((EventGroupCollectListener) item);
                }

                if(item instanceof SignalCollectListener) {
                    instance.register((SignalCollectListener) item);
                }
            });
        }

        return instance;
    }

    public void register(EventGroupCollectListener listener) {
        groupCollectListeners.add(listener);
    }

    public void register(SignalCollectListener listener) {
        signalCollectListeners.add(listener);
    }

    public void modelForEach(Consumer<EventGroupCollectListener> consumer) {
        groupCollectListeners.forEach(consumer);
    }


    public void signalForEach(Consumer<SignalCollectListener> consumer) {
        signalCollectListeners.forEach(consumer);
    }

    public static CollectListenerManager getInstance() {
        return instance;
    }
}
