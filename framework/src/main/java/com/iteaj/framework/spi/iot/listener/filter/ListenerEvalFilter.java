package com.iteaj.framework.spi.iot.listener.filter;

import java.util.List;

/**
 * 监听执行过滤器
 */
public interface ListenerEvalFilter<T> {

    /**
     * 过滤器名称
     * @return
     */
    String getFilterName();

    /**
     * 是否过滤
     * @param service
     * @param param
     * @return
     */
    boolean filter(ListenerEvalScriptService service, T param);
}
