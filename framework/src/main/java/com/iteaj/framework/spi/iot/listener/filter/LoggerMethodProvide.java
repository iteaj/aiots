package com.iteaj.framework.spi.iot.listener.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerMethodProvide {

    private Logger logger = LoggerFactory.getLogger("scriptLogger");

    public void log(String msg, String... params) {
        logger.debug(msg, params);
    }

    public void info(String msg, String... params) {
        logger.info(msg, params);
    }

    public void warn(String msg, String... params) {
        logger.warn(msg, params);
    }

    public void error(String msg, String... params) {
        logger.error(msg, params);
    }
}
