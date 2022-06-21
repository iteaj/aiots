package com.iteaj.iboot.msn.iot.debug.plc;

import com.iteaj.iboot.msn.iot.debug.DebugHandle;
import com.iteaj.iboot.msn.iot.debug.DebugWebsocketWrite;
import org.springframework.stereotype.Component;

@Component
public class PlcDebugHandle implements DebugHandle<PlcDebugModel> {

    @Override
    public String type() {
        return "plc";
    }

    @Override
    public void handle(PlcDebugModel model, DebugWebsocketWrite write) {

    }
}
