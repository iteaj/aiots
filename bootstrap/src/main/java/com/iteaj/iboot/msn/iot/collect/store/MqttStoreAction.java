package com.iteaj.iboot.msn.iot.collect.store;

import com.alibaba.fastjson.JSONObject;
import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectData;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayConnectProperties;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayHead;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayMessage;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayProtocol;
import com.iteaj.iot.message.DefaultMessageBody;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;
import java.util.Map;

public class MqttStoreAction extends StoreAction {

    private static ExpressionParser parser = new SpelExpressionParser();

    @Override
    public String getName() {
        return IotConsts.STORE_ACTION_MQTT;
    }

    @Override
    public String getDesc() {
        return "MQTT服务";
    }

    @Override
    public void configValidate(Map<String, Object> jsonConfig) {
        if(!(jsonConfig.get("topic") instanceof String)) {
            throw new CollectException("未配置参数[topic]");
        }
        if(!(jsonConfig.get("clientId") instanceof String)) {
            throw new CollectException("未配置参数[clientId]");
        }
        if(!(jsonConfig.get("username") instanceof String)) {
            throw new CollectException("未配置参数[username]");
        }
        if(!(jsonConfig.get("password") instanceof String)) {
            throw new CollectException("未配置参数[password]");
        }
        if(!(jsonConfig.get("host") instanceof String)) {
            throw new CollectException("未配置参数[host]");
        }
        if(!(jsonConfig.get("port") instanceof Integer)) {
            throw new CollectException("未配置参数[port]");
        }
    }

    @Override
    public void store(CollectDetail detail, List<CollectData> data) {
        DeviceDto store = detail.getStore();
        data.forEach(item -> {
            Map<String, Object> config = detail.resolveConfig();
            // 解析数据表表名
            String topic = parser.parseExpression((String) config.get("topic")).getValue(item, String.class);

            // 创建连接配置信息
            MqttGatewayConnectProperties connectProperties = new MqttGatewayConnectProperties(store.getIp()
                    , (Integer) config.get("port"), (String) config.get("clientId"), (String) config.get("topic"));
            connectProperties.setPassword((String) config.get("password"));
            connectProperties.setUsername((String) config.get("username"));

            // 创建请求报文
            MqttGatewayHead head = new MqttGatewayHead((String) config.get("clientId"));
            DefaultMessageBody body = new DefaultMessageBody(JSONObject.toJSONBytes(item));
            MqttGatewayMessage gatewayMessage = new MqttGatewayMessage(head, body, topic);

            // 发送请求给mqtt服务器
            new MqttGatewayProtocol(gatewayMessage).request(connectProperties);
        });
    }
}
