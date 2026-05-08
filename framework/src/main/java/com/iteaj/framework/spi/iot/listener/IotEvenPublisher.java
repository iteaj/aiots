package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.thread.IBootThreadManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Iot事件发布者
 * @see IotListener
 * @see IotEventType
 */
public class IotEvenPublisher {

    private static List<IotListener> listeners = new ArrayList<>(64);
    private static Logger logger = LoggerFactory.getLogger(IotEvenPublisher.class);

    /**
     * 注册监听
     * @param listener
     */
    public static void register(IotListener listener) {
        listeners.add(listener);
        Collections.sort(listeners);
    }

    /**
     * 同步发布
     * @param type
     * @param value
     */
    public static void publish(IotEventType type, IotEventPayload value) {
        listeners.stream().filter(item -> item.isEventMatcher(type)).forEach(item -> {
            if(item instanceof IotAsyncListener) {
                asyncPublish(type, value, null, (IotAsyncListener) item);
            } else {
                try {
                    item.onIotEvent(type, value);
                } catch (Exception e) {
                    logger.error("<<< IOT事件发布异常({}) {} - 发布值: {} - 监听对象: {}"
                            , type, type.getDesc(), value, item.getClass().getSimpleName(), e);
                }
            }
        });
    }

    /**
     * 异步发布
     * @param type
     * @param value
     * @param callback
     */
    public static void publish(IotEventType type, IotEventPayload value, Consumer<Object> callback) {
        listeners.stream()
                .filter(item -> item instanceof IotAsyncListener && item.isEventMatcher(type))
                .forEach(item -> asyncPublish(type, value, callback, (IotAsyncListener) item));
    }

    private static void asyncPublish(IotEventType type, IotEventPayload value, Consumer<Object> callback, IotAsyncListener item) {
        IBootThreadManger.execute(() -> {
            try {
                item.onIotEvent(type, value, callback);
            } catch (Exception e) {
                if(callback != null) {
                    callback.accept(e);
                }

                logger.error("<<< IOT异步事件发布异常({}) {} - 发布值: {} - 监听对象: {}"
                        , type, type.getDesc(), value, item.getClass().getSimpleName(), e);
            }
        });
    }
}
