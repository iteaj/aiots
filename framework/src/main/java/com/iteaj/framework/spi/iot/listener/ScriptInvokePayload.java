package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;

import java.util.Objects;

public class ScriptInvokePayload implements IotEventPayload{

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 设备指令
     */
    private String cmd;

    /**
     * 告警数据
     */
    private WarnData data;

    /**
     * 执行结果
     */
    private InvokeResult result;

    /**
     * cmd 指令调用
     * warn 告警调用
     */
    private String type;

    protected ScriptInvokePayload(WarnData data) {
        this.data = data;
        this.type = "warn";
    }

    protected ScriptInvokePayload(String uid, String cmd) {
        this.uid = uid;
        this.cmd = cmd;
        this.type = "cmd";
    }

    public static ScriptInvokePayload build(String uid, String cmd) {
        return new ScriptInvokePayload(uid, cmd);
    }

    public static ScriptInvokePayload build(WarnData warnData) {
        return new ScriptInvokePayload(warnData);
    }

    public boolean isWarn() {
        return Objects.equals("warn", this.type);
    }

    public boolean isCmd() {
        return Objects.equals("cmd", this.type);
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

    public WarnData getData() {
        return data;
    }
}
