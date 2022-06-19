package com.iteaj.iboot.msn.iot.debug;

public interface DebugHandle {

    /**
     * 调试类型
     * @return
     */
    String type();

    /**
     * 解析json格式的model对象
     * @param model
     * @return
     */
    DebugModel parse(String model);

    /**
     *
     * @param model
     * @param write
     */
    void handle(DebugModel model, DebugWebsocketWrite write);
}
