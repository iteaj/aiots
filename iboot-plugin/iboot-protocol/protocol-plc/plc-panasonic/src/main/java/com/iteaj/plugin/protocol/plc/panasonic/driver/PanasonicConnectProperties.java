package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;

public class PanasonicConnectProperties extends PlcConnectProperties {

    /**
     * 站号
     */
    private byte station;

    public PanasonicConnectProperties(Integer remotePort, byte station) {
        super(remotePort);
        this.station = station;
    }

    public PanasonicConnectProperties(String remoteHost, Integer remotePort, byte station) {
        super(remoteHost, remotePort);
        this.station = station;
    }

    public byte getStation() {
        return station;
    }

    public void setStation(byte station) {
        this.station = station;
    }
}
