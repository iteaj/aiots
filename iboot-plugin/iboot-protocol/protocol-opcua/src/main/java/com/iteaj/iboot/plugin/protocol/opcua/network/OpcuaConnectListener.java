package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.event.StatusEvent;
import org.apache.plc4x.java.api.listener.ConnectionStateListener;

public class OpcuaConnectListener implements ConnectionStateListener {

    @Override
    public void connected() {
        StatusEvent statusEvent = new StatusEvent(null, null);
        FrameworkManager.publishEvent(statusEvent);
    }

    @Override
    public void disconnected() {

    }
}
