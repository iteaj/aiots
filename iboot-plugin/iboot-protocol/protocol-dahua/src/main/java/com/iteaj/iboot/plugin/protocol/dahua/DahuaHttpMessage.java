package com.iteaj.iboot.plugin.protocol.dahua;

import com.iteaj.iot.client.http.HttpClientMessage;
import com.iteaj.iot.client.http.HttpMethod;
import okhttp3.RequestBody;

import java.util.Map;

public class DahuaHttpMessage extends HttpClientMessage {

    public DahuaHttpMessage(String url, HttpMethod method) {
        super(url, method);
    }

    public DahuaHttpMessage(String url, HttpMethod method, Map<String, Object> queryParam) {
        super(url, method, queryParam);
    }

    public DahuaHttpMessage(String url, Map<String, Object> queryParam, Map<String, String> formBody) {
        super(url, queryParam, formBody);
    }

    public DahuaHttpMessage(String url, Map<String, Object> queryParam, RequestBody requestBody) {
        super(url, queryParam, requestBody);
    }
}
