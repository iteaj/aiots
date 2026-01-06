package com.iteaj.plguin.protocol.hj212.network;

import cn.hutool.core.io.checksum.crc16.CRC16Ansi;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * HJ212数据推送协议
 */
public class HJ212RealtimeProtocol extends ClientInitiativeProtocol<HJ212Message> {

    public HJ212RealtimeProtocol(HJ212Message requestMessage) {
        super(requestMessage);
    }

    /**
     * QN=20160801085857223;ST=91;CN=9014;PW=123456;MN=010000A8900016F000169DC0;Flag=4;CP=&&&&
     * @return
     */
    @Override
    protected HJ212Message doBuildResponseMessage() {
        HJ212MessageHead head = requestMessage().getHead().buildResp();
        String dataMsg = String.format("QN=%s;ST=%s;CN=%s;PW=%s;MN=%s;Flag=%s;CP=&&%s&&", head.getQN()
                , head.getST().getCode(), head.getCN().getCode(), head.getPW(), head.getMN(), "4", "");

        // crc校验
        CRC16Ansi crc16Ansi = new CRC16Ansi();
        crc16Ansi.update(dataMsg.getBytes(StandardCharsets.UTF_8));
        String crc = crc16Ansi.getHexValue(true).toUpperCase();

        String length = String.format("%04d", dataMsg.length());
        String msg = "##" + length + dataMsg + crc + "\r\n";
        HJ212Message message = new HJ212Message(head, new HJ212MessageBody(null));
        message.setMessage(msg.getBytes(StandardCharsets.UTF_8));
        return message;
    }


    /**
     * 格式 QN=20160801085857223;ST=32;CN=1072;PW=123456;MN=010000A8900016F000169DC0;Flag=5;CP=&&NewPW=654321&&
     */
    @Override
    protected void doBuildRequestMessage(HJ212Message requestMessage) {

    }

    public String getItem(String key) {
        return requestMessage().getBody().getItem(key);
    }

    public Set<Map.Entry<String, String>> getItems() {
        return requestMessage().getBody().getData().entrySet();
    }

    public Map<String, String> getData() {
        return requestMessage().getBody().getData();
    }

    @Override
    public ProtocolType protocolType() {
        return requestMessage().getHead().getType();
    }
}
