package com.iteaj.framework.spi.event;

import com.google.common.eventbus.AsyncEventBus;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
public class AsyncGuavaEventBus extends AsyncEventBus implements AsyncPublisher {

    private List<Listener> listeners;

    public AsyncGuavaEventBus(List<Listener> listeners, Executor executor) {
        super("AsyncGuavaEventBus", executor);
        this.listeners = listeners;
        registerListener();
    }

    private void registerListener() {
        if(listeners != null) {
            listeners.forEach(listener -> register(listener));
        }
    }

    @Override
    public void readEvent() {
        /*do nothing*/
    }

    @Override
    public List<Listener> listeners() {
        return listeners;
    }

    @Override
    public void publish(Event event) {
        post(event);
    }
}
