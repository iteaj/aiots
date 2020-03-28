package com.iteaj.framework.utils;

import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;

public class EventUtils {

    private static EventBus eventBus = new DefaultEventBus();

    public static EventBus getEventBus() {
        return eventBus;
    }
}
