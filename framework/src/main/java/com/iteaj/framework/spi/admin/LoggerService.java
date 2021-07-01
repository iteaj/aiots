package com.iteaj.framework.spi.admin;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
public interface LoggerService {

    void record(LoggerEvent entity);

    /**
     * 通过url获取对应的菜单
     * @param url
     * @return
     */
    LoggerMenu getLoggerMenu(String url);
}
