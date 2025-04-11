package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.spi.iot.listener.IotEventPayload;

/**
 * 监听执行过滤器
 */
public interface ListenerEvalFilter<T extends IotEventPayload> {

    /**
     * 过滤器名称
     * @return
     */
    String getFilterName();

    /**
     * 是否过滤
     * @param param
     * @return 返回 {@code null}表示不过滤 其他情况表示过滤
     */
    T filter(T param);
}
