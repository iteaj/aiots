package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.spi.iot.protocol.InvokeResult;

import java.util.function.Consumer;

public class CmdInvokePayload implements IotEventPayload{

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 设备指令
     */
    private String code;

    /**
     * 超时时间
     */
    private long timeout;

    private Consumer<InvokeResult> callback = def -> {};

    public CmdInvokePayload() {
        this(null, null, 1000 * 10);
    }

    public CmdInvokePayload(String uid, String cmd) {
        this(uid, cmd, 1000 * 10);
    }

    public CmdInvokePayload(String uid, String cmd, long timeout) {
        this.uid = uid;
        this.code = cmd;
        this.timeout = timeout;
    }

    public CmdInvokePayload build(Consumer<InvokeResult> callback) {
        this.callback = callback; return this;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Consumer<InvokeResult> getCallback() {
        return callback;
    }
}
