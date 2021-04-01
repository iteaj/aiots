package com.iteaj.framework.spi.event.spring;

import com.iteaj.framework.spi.event.FrameworkEvent;
import org.springframework.context.ApplicationEvent;

/**
 * create time: 2021/3/31
 *
 * @author iteaj
 * @since 1.0
 */
public class SpringEvent extends ApplicationEvent implements FrameworkEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringEvent(Object source) {
        super(source);
    }
}
