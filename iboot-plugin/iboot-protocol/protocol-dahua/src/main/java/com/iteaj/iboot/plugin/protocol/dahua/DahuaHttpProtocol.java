package com.iteaj.iboot.plugin.protocol.dahua;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralRequest;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.iteaj.iot.client.http.ClientHttpProtocol;
import com.iteaj.iot.client.http.HttpMethod;
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
        try {
            Method method = requestMessage().getMethod() == HttpMethod.Get ? Method.GET : Method.POST;
            GeneralRequest generalRequest = new GeneralRequest(requestMessage().getUrl(), method);
//            requestMessage().
            // 发起请求处理应答
            GeneralResponse generalResponse =DahuaICCAutoConfiguration.iClient.doAction(generalRequest, generalRequest.getResponseClass());
        } catch (ClientException e) {
            throw new HttpProtocolException(e.getMessage(), e);
        }

        return this;
    }

    @Override
    public <T> T protocolType() {
        return null;
    }
}
