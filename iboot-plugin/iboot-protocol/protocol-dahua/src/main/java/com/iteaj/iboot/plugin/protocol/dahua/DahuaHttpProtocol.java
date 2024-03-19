package com.iteaj.iboot.plugin.protocol.dahua;

import com.iteaj.iot.client.http.ClientHttpProtocol;
import com.iteaj.iot.client.http.HttpProtocolException;

public class DahuaHttpProtocol extends ClientHttpProtocol<DahuaHttpMessage> {

    public DahuaHttpProtocol(DahuaHttpMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected DahuaHttpMessage doBuildRequestMessage() {
        return this.requestMessage();
    }

    @Override
    protected void doBuildResponseMessage(DahuaHttpMessage message) {

    }

    @Override
    public DahuaHttpProtocol request() throws HttpProtocolException {

        return this;
    }

    @Override
    public <T> T protocolType() {
        return null;
    }
}
