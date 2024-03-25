package com.iteaj.iboot.plugin.protocol.dahua;

import com.alibaba.fastjson.JSONObject;
import com.dahuatech.hutool.http.Method;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.DefaultClient;
import com.dahuatech.icc.oauth.http.IClient;
import com.dahuatech.icc.oauth.model.v202010.GeneralRequest;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.iteaj.iboot.plugin.protocol.dahua.access.DahuaICCProtocolSupplier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@EnableConfigurationProperties(DahuaProperties.class)
public class DahuaICCAutoConfiguration {

    public static IClient iClient;
    private final DahuaProperties properties;

    public DahuaICCAutoConfiguration(DahuaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public IClient defaultClient() throws ClientException {
        iClient = new DefaultClient(properties.getHost(), properties.getUsername()
                , properties.getPassword(),properties.getPwdClientId(), properties.getPwdClientSecret());
        GeneralRequest generalRequest = new GeneralRequest("/evo-apigw/evo-brm/1.2.0/device/subsystem/page", Method.POST);
//        // set http post body
        JSONObject json = new JSONObject();
//        json.put("categorys", new String[]{"1"});
        json.put("types", new String[]{"1_2"});
        json.put("unitTypes", new String[]{"1"});
        generalRequest.body(json.toJSONString());
        // 发起请求处理应答
        GeneralResponse generalResponse =iClient.doAction(generalRequest, generalRequest.getResponseClass());
        System.out.println(generalResponse);
        return iClient;
    }

    @Bean
    public DahuaICCProtocolSupplier dahuaICCAccessProtocolSupplier() {
        return new DahuaICCProtocolSupplier();
    }

//    @Bean
//    public DahuaICCVideoProtocolSupplier dahuaICCVideoProtocolSupplier() {
//        return new DahuaICCVideoProtocolSupplier();
//    }
}
