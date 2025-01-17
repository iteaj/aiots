package com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff;

/**
 * AMS消息的命令号
 */
public class AmsTcpHeaderFlags {
    /**
     * AmsCommand (AMS_TCP_PORT_AMS_CMD, 0x0000)
     */
    public final static int Command = 0x00;

    /**
     * Port Close command (AMS_TCP_PORT_CLOSE, 0x0001)
     */
    public final static int PortClose = 0x01;

    /**
     * Port connect command (AMS_TCP_PORT_CONNECT, 0x1000)
     */
    public final static int PortConnect = 0x1000;

    /**
     * Router Notification (AMS_TCP_PORT_ROUTER_NOTE, 0x1001)
     */
    public final static int RouterNotification = 0x1001;

    /**
     * Get LocalNetId header
     */
    public final static int GetLocalNetId = 0x1002;
}
