package com.iteaj.iboot.plugin.protocol.dahua.access;

import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaHttpMessage;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaHttpProtocol;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaICCProtocolType;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.http.HttpMethod;
import com.iteaj.iot.consts.ExecStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class DahuaICCProtocolModelApi extends AbstractFuncProtocolModelApi {

    public DahuaICCProtocolModelApi(String code, String name, String remark) {
        super(code, name, FuncType.W, TriggerMode.passive, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        DahuaICCProtocolType protocolType = DahuaICCProtocolType.valueOf(getCode());
        Map<String, Object> body = new HashMap<>();
        DahuaHttpMessage message = new DahuaHttpMessage(protocolType.getUrl(), null, body);
        if(protocolType == DahuaICCProtocolType.realtime) {
            Map<String, String> data = new HashMap<>();
            body.put("data", data);
            getDownConfig().forEach((key, value) -> {
                String keyValue = arg.getString(key);
                data.put(key, keyValue);
            });
            data.put("channelId", arg.getDeviceSn());
        } else if(Objects.equals(this.getCode(), DahuaICCProtocolType.OperateDirect.name())) {
            Map<String, String> data = new HashMap<>();
            data.put("extend", "");
            body.put("data", data);
            getDownConfig().forEach((key, value) -> {
                String keyValue = arg.getString(key);
                data.put(key, keyValue);
            });
            data.put("channelId", arg.getDeviceSn());
        } else {
            getDownConfig().forEach((key, value) -> {
                body.put(key, new String[] {arg.getString(key)});
            });
        }
        return new DahuaHttpProtocol(message);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        DahuaHttpProtocol httpProtocol = (DahuaHttpProtocol) protocol;
        httpProtocol.request();
        InvokeResult status;
        if(httpProtocol.getStatus() == ExecStatus.success) {
            JSONObject value = new JSONObject();
            JSONObject data = httpProtocol.getValue().getJSONObject("data");
            if(Objects.equals(getCode(), DahuaICCProtocolType.realtime)) {
                // 获取播放地址
                String url = data.getString("url");
                value.put("url", url);
            }

            value.put("status", true);
            status = InvokeResult.success(value, protocol);
        } else {
            status = InvokeResult.status(httpProtocol.getStatus(), protocol);
            status.setReason(httpProtocol.getReason());
        }
        result.accept(status);
    }
}
