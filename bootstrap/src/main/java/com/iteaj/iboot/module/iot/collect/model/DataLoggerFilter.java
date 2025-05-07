package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.logger.LoggerFilter;
import com.iteaj.framework.logger.PushParams;

import static com.iteaj.iboot.module.iot.utils.IotLogger.REPORT_NAME;
import static com.iteaj.iboot.module.iot.utils.IotLogger.SOURCE_NAME;

/**
 * 数据采集和数据上报过滤
 */
public class DataLoggerFilter implements LoggerFilter {

    @Override
    public String type() {
        return "collect";
    }

    @Override
    public String name() {
        return SOURCE_NAME;
    }

    @Override
    public boolean filter(PushParams params) {
        return params.getType().equals(name()) || params.getType().equals(REPORT_NAME);
    }


}
