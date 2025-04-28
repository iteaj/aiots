package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.spi.iot.protocol.InvokeResult;

public class CmdInvokePayload implements IotEventPayload{

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 设备指令
     */
    private String cmd;

    /**
     * 执行结果
     */
    private InvokeResult result;

    public CmdInvokePayload(String uid, String cmd) {
        this.uid = uid;
        this.cmd = cmd;
    }

    public String getUid() {
        return uid;
    }

    public String getCmd() {
        return cmd;
    }

    public InvokeResult getResult() {
        return result;
    }

    public void setResult(InvokeResult result) {
        this.result = result;
    }
}
