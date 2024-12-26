package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;

public class SiemensConnectProperties extends PlcConnectProperties {

    /**
     * 西门子型号
     */
    private SiemensPLCS model;

    public SiemensConnectProperties(Integer remotePort, SiemensPLCS model) {
        super(remotePort);
        this.model = model;
    }

    public SiemensConnectProperties(String remoteHost, Integer remotePort, SiemensPLCS model) {
        super(remoteHost, remotePort);
        this.model = model;
    }

    public SiemensConnectProperties(String remoteHost, Integer remotePort, String connectKey, SiemensPLCS model) {
        super(remoteHost, remotePort, connectKey);
        this.model = model;
    }

    public SiemensConnectProperties(String remoteHost, Integer remotePort, Integer localPort, SiemensPLCS model) {
        super(remoteHost, remotePort, localPort);
        this.model = model;
    }

    public SiemensPLCS getModel() {
        return model;
    }
}
