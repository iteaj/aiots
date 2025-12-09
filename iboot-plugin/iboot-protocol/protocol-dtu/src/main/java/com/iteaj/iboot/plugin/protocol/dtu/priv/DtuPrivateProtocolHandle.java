package com.iteaj.iboot.plugin.protocol.dtu.priv;

import com.iteaj.iot.client.ClientProtocolHandle;
import com.iteaj.iot.server.ServerMessage;
import com.iteaj.iot.server.ServerProtocolHandle;
import com.iteaj.iot.server.dtu.protocol.DtuPrivateProtocol;
import org.springframework.stereotype.Component;

public class DtuPrivateProtocolHandle implements ServerProtocolHandle<DtuPrivateProtocol> {
    @Override
    public Object handle(DtuPrivateProtocol protocol) {
        ServerMessage serverMessage = protocol.requestMessage();
        System.out.println("设备编号: " + protocol.getEquipCode()+ " - 收到数据: " + new String(serverMessage.getMessage()));
        return null;
    }
}
