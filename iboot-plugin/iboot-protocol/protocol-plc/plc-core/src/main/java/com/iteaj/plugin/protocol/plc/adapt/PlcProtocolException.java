package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.ProtocolException;

public class PlcProtocolException extends ProtocolException {

    public PlcProtocolException(String message) {
        super(message);
    }

    public PlcProtocolException(String message, Object protocol) {
        super(message, protocol);
    }

    public PlcProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlcProtocolException(Throwable cause) {
        super(cause);
    }

    public PlcProtocolException(Throwable cause, Object protocol) {
        super(cause, protocol);
    }

    public PlcProtocolException(String message, Throwable cause, Object protocol) {
        super(message, cause, protocol);
    }
}
