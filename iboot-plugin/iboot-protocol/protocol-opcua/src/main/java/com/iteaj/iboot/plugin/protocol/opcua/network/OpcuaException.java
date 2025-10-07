package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.client.ClientProtocolException;

public class OpcuaException extends ClientProtocolException {

    public OpcuaException(String message) {
        super(message);
    }

    public OpcuaException(Throwable cause) {
        super(cause);
    }

    public OpcuaException(String message, Throwable cause) {
        super(message, cause);
    }
}
