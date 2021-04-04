package com.iteaj.framework.spi.event;

import java.util.EventListener;

/**
 * create time: 2020/4/21
 *  事件监听器
 * @author iteaj
 * @since 1.0
 */
public interface FrameworkListener<E extends FrameworkEvent> extends EventListener {

    /**
     *  监听到事件的处理方法
     * @param event
     */
    void onFrameworkEvent(E event);
}
