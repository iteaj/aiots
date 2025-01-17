package com.iteaj.plugin.protocol.plc.core.types;

/**
 * 远程对象关闭的异常信息<br />
 * Exception information of remote object close
 */
public class RemoteCloseException extends Exception {
    public RemoteCloseException() {
        super("Remote Closed Exception");
    }
}
