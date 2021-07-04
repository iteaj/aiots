package com.iteaj.framework.logger;

import com.iteaj.framework.spi.admin.MenuResource;

/**
 * create time: 2021/6/29
 *
 * @author iteaj
 * @since 1.0
 */
public interface LoggerMenu extends MenuResource {

    /**
     * 是否采集日志
     * @return
     */
    boolean isCollect();

    /**
     * 日志说明
     * @return
     */
    String getDesc();
}
