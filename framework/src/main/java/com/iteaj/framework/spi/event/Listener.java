package com.iteaj.framework.spi.event;

import java.util.EventListener;

/**
 * create time: 2020/4/21
 *  事件监听器
 * @author iteaj
 * @since 1.0
 */
public interface Listener<E extends Event> extends EventListener {

    /**
     * 是否接收此事件
     * @param event
     * @return
     */
    boolean accepts(Event event);

    /**
     *
     * @param event
     */
    void onEvent(E event);
}
