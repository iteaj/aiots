package com.iteaj.framework.spi.event;

import java.util.EventObject;

/**
 * create time: 2020/4/21
 *  事件
 * @author iteaj
 * @since 1.0
 */
public class Event extends EventObject implements FrameworkEvent {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public Event(Object source) {
        super(source);
    }
}
