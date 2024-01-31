package com.iteaj.framework.spi.iot.protocol;

import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

/**
 * 接口执行结果
 */
public class InvokeResult {

    /**
     * 返回值
     */
    private Object value;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 执行状态
     */
    private ExecStatus status;

    /**
     * 执行协议
     */
    private Protocol protocol;

    public InvokeResult() { }

    public InvokeResult(String reason) {
       this(reason, ExecStatus.fail);
    }

    public InvokeResult(String reason, ExecStatus status) {
        this.reason = reason;
        this.status = status;
    }

    public InvokeResult(Object value) {
        this(value, "采集成功", ExecStatus.success);
    }

    public InvokeResult(Object value, String reason, ExecStatus status) {
        this.value = value;
        this.reason = reason;
        this.status = status;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ExecStatus getStatus() {
        return status;
    }

    public void setStatus(ExecStatus status) {
        this.status = status;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public InvokeResult setProtocol(Protocol protocol) {
        this.protocol = protocol; return this;
    }
}
