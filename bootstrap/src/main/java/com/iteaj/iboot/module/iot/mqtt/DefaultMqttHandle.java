package com.iteaj.iboot.module.iot.mqtt;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;
import com.iteaj.iboot.module.iot.entity.ModelAttrDict;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iot.client.ClientProtocolHandle;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttConnectProperties;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttSubscribeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DefaultMqttHandle implements ClientProtocolHandle<DefaultMqttSubscribeProtocol>, ApplicationReadyListener {

    private Long syncTime; // 同步时间
    private final static String clientId = "clientId";
    private final static String deviceSn = "deviceSn";
    private final static String gatewaySn = "gatewaySn";
    private final static String DEFAULT_MQTT_IMPL = "MQTT_DEFAULT_IMPL";
    private IotCacheManager cacheManager;
    private IDeviceService deviceService;
    private IProductService productService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, List<ModelAttrDict>> subscribeApis = new HashMap<>();

    @Override
    public Object handle(DefaultMqttSubscribeProtocol protocol) {
        if(CollectionUtil.isEmpty(subscribeApis)) {
            syncSubscribeApi(); // 如果没有订阅接口马上同步一次
        } else {
            // 同步间隔大于5 * 60秒再次同步
            long l = System.currentTimeMillis() - syncTime;
            if(TimeUnit.MILLISECONDS.toSeconds(l) >= 5 * 60) {
                syncSubscribeApi();
            }
        }

        if(CollectionUtil.isNotEmpty(subscribeApis)) {
            subscribeApis.forEach((topic, dicts) -> {
                String[] modelTopic = topic.split("/");
                String[] subscribeTopic = protocol.getTopic().split("/"); // 设备发布的topic
                if(modelTopic.length != subscribeTopic.length) { // 不匹配
                    return;
                }

                DefaultMqttConnectProperties properties = protocol.requestMessage().getProperties();
                String clientIdValue = null, deviceSnValue = null, gatewaySnValue = properties.connectKey();
                for (int i = 0; i < modelTopic.length; i++) {
                    String item = modelTopic[i];
                    String subscribe = subscribeTopic[i];
                    if(item.startsWith("{") && item.endsWith("}")) { // 如果是占位符, 解析占位符的值
                        if(item.contains(deviceSn)) {
                            deviceSnValue = subscribe;
                        } else if(item.contains(clientId)) {
                            clientIdValue = subscribe;
                            // 只匹配同一个clientId下的
                            if(!properties.getClientId().equals(clientIdValue)) {
                                return;
                            }
                        } else if(item.contains(gatewaySn)) {
                            gatewaySnValue = subscribe;
                            // 只匹配同一网关下的
                            if(!gatewaySnValue.equals(properties.connectKey())) {
                                return;
                            }
                        }
                    } else if(item.equals(subscribe)) {
                        continue; // 如果相等则继续判断
                    } else {
                        return; // 只要有一个项不相等则不匹配
                    }
                }

                DeviceKey deviceKey = DeviceKey.build(deviceSnValue, gatewaySnValue);
                RealtimeStatus device = cacheManager.get(DEFAULT_MQTT_IMPL, deviceKey);

                try {
                    if(device != null) {
                        if(device.getStatus() == DeviceStatus.offline) {
                            device.setStatus(DeviceStatus.online); // 设备上线
                            this.deviceService.updateDeviceStatus(device);
                        }

                        resolvePayloadValue(dicts, protocol, gatewaySnValue, deviceSnValue, device);
                    } else {
                        logger.error("处理mqtt报文失败 设备不存在 - 设备: {} - 设备发布: {} - 平台订阅: {}", deviceKey, protocol.getTopic(), topic);
                    }
                } catch (Exception e) {
                    logger.error("处理mqtt报文负载到物模型属性失败 未知错误 - 设备发布: {} - 平台订阅: {}", protocol.getTopic(), topic, e);
                }
            });
        }

        return null;
    }

    private void resolvePayloadValue(List<ModelAttrDict> dicts, DefaultMqttSubscribeProtocol protocol
            , String gatewaySnValue, String deviceSnValue, RealtimeStatus device) {
        Date date = new Date();
        byte[] message = protocol.requestMessage().getMessage();
        JSONObject jsonObject = JSONObject.parseObject(new String(message, Charset.forName("utf-8")));
        List<SignalOrFieldValue> fieldValues = dicts.stream().map(item -> {
            Object value = jsonObject.get(item.getAttrField());
            return new SignalOrFieldValue(Long.valueOf(item.getDictValue()), item.getAttrField(), date, value);
        }).collect(Collectors.toList());

        // 发布模型属性的事件
        DataSupplier dataSupplier = new DataSupplier(DEFAULT_MQTT_IMPL, DeviceKey.build(deviceSnValue, gatewaySnValue));
        dataSupplier.setValues(fieldValues);
        CollectListenerManager.getInstance().modelAttrPublish(modelAttrListener -> {
            modelAttrListener.supplier(dataSupplier, device);
        });
    }

    private void syncSubscribeApi() {
        this.syncTime = System.currentTimeMillis();
        productService.listByProtocolCode(DEFAULT_MQTT_IMPL).forEach(productDto -> {
            if(CollectionUtil.isEmpty(productDto.getEventApis())) {
                return; // 没有订阅事件直接返回
            }

            if(CollectionUtil.isEmpty(productDto.getAttrs())) {
                logger.error("解析mqtt物模型失败 产品没有配置模型属性 - 产品: {}", productDto.getName(), new IllegalArgumentException("产品没有配置模型属性"));
                return;
            }

            // 产品没有启用则不进行匹配
            if(productDto.getStatus() == FuncStatus.disabled) {
                return;
            }

            List<ModelApi> eventApis = productDto.getEventApis();
            if(CollectionUtil.isNotEmpty(eventApis)) {
                eventApis.forEach(api -> {
                    if(CollectionUtil.isNotEmpty(api.getUpConfig())) {
                        String topic = null;
                        List<ModelAttrDict> dicts = null;
                        for (int i = 0; i < api.getUpConfig().size(); i++) {
                            ModelApiConfig config = api.getUpConfig().get(i);
                            if("topic".equals(config.getProtocolAttrField())) {
                                if(config.getModelAttrId() != null) {
                                    logger.error("解析mqtt物模型协议失败 topic不支持属性提及 - 产品: {}", productDto.getName(), new IllegalArgumentException());
                                    return;
                                } else {
                                    topic = config.getValue();
                                }
                            } else if("payload".equals(config.getProtocolAttrField())) {
                                if(config.getModelAttrId() == null) {
                                    logger.error("解析mqtt物模型失败 payload没有配置属性 - 产品: {}", productDto.getName(), new IllegalArgumentException("payload没有配置属性"));
                                    return;
                                }

                                if(CollectionUtil.isEmpty(config.getDicts())) {
                                    logger.error("解析mqtt物模型失败 payload属性没有配置项 - 产品: {}", productDto.getName(), new IllegalArgumentException("payload属性没有配置项"));
                                    return;
                                }

                                dicts = config.getDicts();
                            }
                        }

                        subscribeApis.put(topic, dicts);
                    }
                });
            }
        });
    }

    @Override
    public void started(ApplicationContext context) {
        cacheManager = context.getBean(IotCacheManager.class);
        deviceService = context.getBean(IDeviceService.class);
        productService = context.getBean(IProductService.class);
        this.syncSubscribeApi();
    }
}
