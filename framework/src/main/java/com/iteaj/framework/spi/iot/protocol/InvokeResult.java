package com.iteaj.framework.spi.iot.protocol;

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

    public InvokeResult() { }

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
}
