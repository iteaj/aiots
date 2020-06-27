package com.iteaj.framework.spi.admin.event;

import com.google.common.eventbus.Subscribe;
import com.iteaj.framework.spi.event.Event;
import com.iteaj.framework.spi.event.Listener;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * create time: 2020/6/20
 *  用户上线 事件监听
 * @author iteaj
 * @since 1.0
 */
public class OnlineListener implements Listener<OnlineEvent> {

    private Consumer<OnlineEvent> function;

    public OnlineListener(Consumer<OnlineEvent> function) {
        this.function = function;
    }

    @Override
    public boolean accepts(Event event) {
        return event instanceof OnlineEvent;
    }

    @Override
    @Subscribe
    public void onEvent(OnlineEvent event) {
        this.function.accept(event);
    }
}
