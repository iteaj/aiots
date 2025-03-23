package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolException;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;

public class MelsecClient extends PlcClientAdapter {

    public MelsecClient(PlcClientComponent clientComponent, MelsecConnectProperties connectProperties) {
        super(clientComponent, connectProperties);
    }

    @Override
    protected NetworkDoubleBase doInit(PlcConnectProperties config) {
        MelsecConnectProperties melsec = (MelsecConnectProperties) config;
        if(melsec.getProtocolType() == MelsecProtocolType.A1E) {
            return new MelsecA1ENet(config.getHost(), config.getPort());
        } else if(melsec.getProtocolType() == MelsecProtocolType.Qna3C) {
            return new MelsecA3CNetOverTcp(melsec.getHost(), melsec.getPort());
        } else if(melsec.getProtocolType() == MelsecProtocolType.Qna3E) {
            return new MelsecMcNet(config.getHost(), config.getPort());
        } else if(melsec.getProtocolType() == MelsecProtocolType.Qna3E_R) {
            return new MelsecMcRNet(config.getHost(), config.getPort());
        } else if(melsec.getProtocolType() == MelsecProtocolType.FxLinks) {
            return new MelsecFxLinksOverTcp(config.getHost(), config.getPort());
        } else if(melsec.getProtocolType() == MelsecProtocolType.FxSerial) {
            return new MelsecFxSerialOverTcp(config.getHost(), config.getPort());
        }else {
            throw new PlcProtocolException("不支持的三菱类型["+melsec.getProtocolType()+"]");
        }
    }
}
