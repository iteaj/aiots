package com.iteaj.plugin.protocol.plc.ab.profinet;

/**
 * AB PLC的cip通信实现类，适用Micro800系列控制系统<br />
 * AB PLC's cip communication implementation class, suitable for Micro800 series control system
 */
public class AllenBradleyMicroCip extends AllenBradleyNet {

    /**
     * 实例化一个默认的对象
     */
    public AllenBradleyMicroCip( ){

    }

    /**
     * 指定IP地址及端口来实例化一个对象，其中端口号默认为 44818
     * @param ipAddress IP地址
     * @param port 端口
     */
    public AllenBradleyMicroCip( String ipAddress, int port ){
        super(ipAddress, port);
    }

    protected byte[] PackCommandService( byte[] portSlot, byte[]... cips ) {
        return AllenBradleyHelper.PackCleanCommandService( portSlot, cips );
    }

    public String toString( ) {
        return "AllenBradleyMicroCip[" + getIpAddress() + ":" + getPort() + "]";
    }
}
