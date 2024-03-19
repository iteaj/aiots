package com.iteaj.iboot.plugin.protocol.dahua;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.http.HttpClient;
import com.iteaj.iot.client.http.HttpClientComponent;
import com.iteaj.iot.client.http.HttpClientConnectProperties;
import com.iteaj.iot.client.http.okhttp.OkHttpClient;

public class DahuaOpenComponent extends HttpClientComponent<DahuaHttpMessage> {

    @Override
    public HttpClient getClient() {
        return null;
    }

    @Override
    protected HttpClient doCreateClient(ClientConnectProperties config) {
        return new OkHttpClient((HttpClientConnectProperties) config, this);
    }

    @Override
    public String getName() {
        return "大华ICC";
    }

    @Override
    public String getDesc() {
        return "大华ICC开放平台协议";
    }
}
