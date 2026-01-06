package com.iteaj.plguin.protocol.hj212.network;

import cn.hutool.core.io.checksum.crc16.CRC16Ansi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.server.protocol.ServerInitiativeProtocol;
import com.iteaj.iot.utils.ByteUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HJ212RequestProtocol extends ServerInitiativeProtocol<HJ212Message> {

    private Map<String, String> result;
    public HJ212RequestProtocol(HJ212Message message) {
        this.setRequestMessage(message);
    }

    @Override
    protected HJ212Message doBuildRequestMessage() throws IOException {
        HJ212MessageHead head = requestMessage().getHead();
        HJ212MessageBody body = requestMessage().getBody();
        String bodyValue = body.getData().entrySet().stream()
                .map(item -> item.getKey() + "=" + item.getValue())
                .collect(Collectors.joining(","));
        String dataMsg = String.format("QN=%s;ST=%s;CN=%s;PW=%s;MN=%s;Flag=%s;CP=&&%s&&", head.getQN()
                , head.getST().getCode(), head.getCN().getCode(), head.getPW(), head.getMN(), head.getFlag(), bodyValue);

        // crc校验
        CRC16Ansi crc16Ansi = new CRC16Ansi();
        crc16Ansi.update(dataMsg.getBytes(StandardCharsets.UTF_8));
        String crc = crc16Ansi.getHexValue(true).toUpperCase();

        String length = String.format("%04d", dataMsg.length());
        String msg = "##" + length + dataMsg + crc + "\r\n";
        requestMessage().setMessage(msg.getBytes(StandardCharsets.UTF_8));
        return requestMessage();
    }

    @Override
    protected void doBuildResponseMessage(HJ212Message message) {
        Consumer<InvokeResult> result = (Consumer<InvokeResult>) getParam("result");
        if(getExecStatus() == ExecStatus.success) {
            HJ212CmdCode type = message.getHead().getType();
            HJ212SysCode sysCode = message.getHead().getST();
            HJ212MessageBody body = message.getBody();
            if(type == HJ212CmdCode.请求应答) {
                if(!body.isQnRtnSuccess()) {
                    if(result != null) {
                        result.accept(InvokeResult.fail(body.getQnRtnMsg(), this));
                    }
                }
            } else if(type == HJ212CmdCode.执行结果) {
                if(body.isExeRtnSuccess()) {
                    if(result != null) {
                        result.accept(InvokeResult.success(body.getExeRtnMsg(), this));
                    }
                } else {
                    if(result != null) {
                        result.accept(InvokeResult.fail(body.getExeRtnMsg(), this));
                    }
                }
            } else { // 请求结果
                this.result = message.getBody().getData();
            }
        } else {
            if(result != null) {
                result.accept(InvokeResult.status(getExecStatus(), this));
            }
        }
    }

    @Override
    public ProtocolType protocolType() {
        return requestMessage().getHead().getType();
    }
}
