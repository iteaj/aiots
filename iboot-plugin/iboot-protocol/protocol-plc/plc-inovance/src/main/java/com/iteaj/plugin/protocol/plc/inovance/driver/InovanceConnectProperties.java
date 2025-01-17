package com.iteaj.plugin.protocol.plc.inovance.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.inovance.profinet.InovanceSeries;

public class InovanceConnectProperties extends PlcConnectProperties {

    /**
     * 站号
     */
    private byte station;

    /**
     * 序列
     */
    private InovanceSeries series;

    public InovanceConnectProperties(String remoteHost, Integer remotePort, byte station, InovanceSeries series) {
        super(remoteHost, remotePort);
        this.station = station;
        this.series = series;
    }

    public byte getStation() {
        return station;
    }

    public void setStation(byte station) {
        this.station = station;
    }

    public InovanceSeries getSeries() {
        return series;
    }

    public void setSeries(InovanceSeries series) {
        this.series = series;
    }
}
