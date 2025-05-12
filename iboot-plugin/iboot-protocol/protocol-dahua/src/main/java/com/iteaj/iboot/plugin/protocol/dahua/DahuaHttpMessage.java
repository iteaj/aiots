package com.iteaj.iboot.plugin.protocol.dahua;

import com.alibaba.fastjson2.JSONObject;
import com.iteaj.iot.client.http.HttpClientMessage;
import com.iteaj.iot.client.http.HttpMethod;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

public class DahuaHttpMessage extends HttpClientMessage {

    private Map<String, Object> formBody = new HashMap<>();

    public DahuaHttpMessage() { }

    public DahuaHttpMessage(String url, Map<String, Object> queryParam) {
        super(url, HttpMethod.Get, queryParam);
    }

    public DahuaHttpMessage(String url, Map<String, Object> queryParam, Map<String, Object> formBody) {
        super(url, queryParam, (Map<String, String>) null);
        this.formBody = formBody;
    }

    public Map<String, Object> getFormBody() {
        return formBody;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(getCode() != 0 || cause() != null) { // 响应
            if(cause() != null) {
                sb.append("执行异常: ").append(cause().getMessage());
            } else {
                sb.append(getContent());
            }
        } else { // 请求
            sb.append("method=").append(getMethod());
            if(getQueryParam() != null) {
                sb.append(", ").append("query={");
                getQueryParam().forEach((key, value) -> {
                    sb.append(key).append('=').append(value).append(", ");
                });
                sb.append('}');
            }

            if(formBody != null) {
                sb.append(", ").append("body=").append(JSONObject.toJSONString(formBody));
            }
        }

        return sb.toString();
    }
}
