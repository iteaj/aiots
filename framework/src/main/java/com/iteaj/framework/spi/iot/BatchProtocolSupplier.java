package com.iteaj.framework.spi.iot;

/**
 * 支持批量读取操作的协议驱动
 */
public interface BatchProtocolSupplier {

    /**
     * 获取批量操作的api
     * @return
     */
    BatchEventModelApi getBatchEventModelApi();

}

