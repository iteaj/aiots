package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.consts.ExecStatus;

public class BatchResult extends InvokeResult {

    public BatchResult() { }

    public BatchResult(DataSupplier value) {
        super(value);
        this.setStatus(ExecStatus.success);
    }

    public BatchResult(String reason, ExecStatus status) {
        super(reason, status);
    }

    @Override
    public DataSupplier getValue() {
        return (DataSupplier) super.getValue();
    }

    public BatchResult build(InvokeResult result) {
        this.setReason(result.getReason());
        this.setStatus(result.getStatus());
        this.setProtocol(result.getProtocol());
        return this;
    }
}
