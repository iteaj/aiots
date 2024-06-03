package com.iteaj.iboot.plugin.protocol.dahua;

import com.alibaba.fastjson.JSONObject;
import com.dahuatech.hutool.http.Method;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralRequest;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.ProtocolHandle;
import com.iteaj.iot.client.http.HttpClientProtocol;
import com.iteaj.iot.client.http.HttpMethod;
import com.iteaj.iot.client.http.HttpProtocolException;
import com.iteaj.iot.consts.ExecStatus;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DahuaHttpProtocol extends HttpClientProtocol<DahuaHttpMessage> {

    private JSONObject value;
    public DahuaHttpProtocol(DahuaHttpMessage requestMessage) {
        super(requestMessage);
    }


    @Override
    protected DahuaHttpMessage doBuildRequestMessage() {
        return this.requestMessage();
    }

    @Override
    protected DahuaHttpMessage doBuildResponseMessage(DahuaHttpMessage message) {
        return null;
    }

    @Override
    public DahuaHttpProtocol request() throws HttpProtocolException {
        ProtocolHandle handle = this.getHandle();
        if(handle == null) {
            this.setHandle(handle = FrameworkManager.getProtocolHandle(getClass()));
        }

        try {
            Method method = requestMessage().getMethod() == HttpMethod.Get ? Method.GET : Method.POST;
            GeneralRequest generalRequest = new GeneralRequest(requestMessage().getUrl(), method);
            if(method == Method.GET) {
                generalRequest.form(requestMessage().getQueryParam());
            } else {
                Map<String, Object> formBody = requestMessage().getFormBody();
                generalRequest.setBody(JSONObject.toJSONString(formBody));
            }

            // 发起请求处理应答
            GeneralResponse generalResponse;
            try {
                generalResponse = DahuaICCAutoConfiguration.iClient.doAction(generalRequest, generalRequest.getResponseClass());
                this.value = JSONObject.parseObject(generalResponse.getResult());
                this.setResponseMessage((DahuaHttpMessage) new DahuaHttpMessage().build(200, generalResponse.getResult()));
                if(generalResponse.isSuccess()) {
                    this.setStatus(ExecStatus.success);
                } else {
                    this.setStatus(ExecStatus.fail);
                    this.setReason(this.value.getString("desc"));
                }

                if(handle != null) {
                    handle.handle(this);
                }
            } catch (ClientException e) {
                this.setStatus(ExecStatus.fail);
                this.setResponseMessage((DahuaHttpMessage) new DahuaHttpMessage().build(e));
            }
        } catch (ClientException e) {
            throw new HttpProtocolException(e.getMessage(), e);
        }

        return this;
    }

    public JSONObject getValue() {
        return value;
    }

    @Override
    public <T> T protocolType() {
        return null;
    }
}
