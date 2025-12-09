package com.iteaj.iboot.plugin.protocol.dtu.priv;

import com.iteaj.iot.server.ServerMessage;
import com.iteaj.iot.server.ServerProtocolHandle;
import com.iteaj.iot.utils.ByteUtil;

public class DtuPrivateProtocolHandle implements ServerProtocolHandle<DtuInitiativeProtocol> {

    @Override
    public Object handle(DtuInitiativeProtocol protocol) {
        ServerMessage serverMessage = protocol.requestMessage();
        System.out.println("DTU主动上报数据 - 设备编号: " + protocol.getEquipCode()+ " - 数据: " + ByteUtil.bytesToHexByFormat(serverMessage.getMessage()));
        return null;
    }
}
