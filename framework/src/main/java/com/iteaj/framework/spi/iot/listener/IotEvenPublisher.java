package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.spi.iot.listener.filter.ListenerEvalFilter;
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
                IBootThreadManger.getInstance().getTaskExecutor().execute(() -> {
                    try {
                        doPublish(type, value, item);
                    } catch (Exception e) {
                        logger.error("<<< IOT异步事件发布异常({}) {} - 发布值: {} - 监听对象: {}"
                                , type, type.getDesc(), value, item.getClass().getSimpleName(), e);
                    }
                });
            } else {
                try {
                    doPublish(type, value, item);
                } catch (Exception e) {
                    logger.error("<<< IOT事件发布异常({}) {} - 发布值: {} - 监听对象: {}"
                            , type, type.getDesc(), value, item.getClass().getSimpleName(), e);
                }
            }
        });
    }

    private static void doPublish(IotEventType type, IotEventPayload value, IotListener item) {
        if(item instanceof ListenerEvalFilter) {
//            final long l = System.currentTimeMillis();
            final Object filter = ((ListenerEvalFilter) item).filter(value);
//            System.out.println("----- 时间 " + (System.currentTimeMillis() - l));
            if(filter != null) {
                item.onIotEvent(type, filter);
            }
        } else {
            item.onIotEvent(type, value);
        }
    }

    /**
     * 异步发布
     * @param type
     * @param value
     * @param callback
     */
    public static void publish(IotEventType type, IotEventPayload value, Consumer callback) {
        listeners.stream().filter(item -> item instanceof IotAsyncListener && item.isEventMatcher(type)).forEach(item -> {
            IBootThreadManger.getInstance().getTaskExecutor().execute(() -> {
                try {
                    if(item instanceof ListenerEvalFilter) {
                        final Object filter = ((ListenerEvalFilter) item).filter(value);
                        if(filter != null) {
                            ((IotAsyncListener)item).onIotEvent(type, filter, callback);
                        }
                    } else {
                        ((IotAsyncListener)item).onIotEvent(type, value, callback);
                    }
                } catch (Exception e) {
                    if(callback != null) {
                        callback.accept(e);
                    }

                    logger.error("<<< IOT异步事件发布异常({}) {} - 发布值: {} - 监听对象: {}"
                            , type, type.getDesc(), value, item.getClass().getSimpleName(), e);
                }
            });
        });
    }
}
