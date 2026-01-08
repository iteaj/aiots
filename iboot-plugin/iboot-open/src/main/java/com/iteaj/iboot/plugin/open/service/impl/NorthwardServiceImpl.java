package com.iteaj.iboot.plugin.open.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.mapper.NorthwardMapper;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayConnectProperties;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayMessage;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.iteaj.iboot.plugin.open.service.impl.NorthwardServiceImpl.CACHE_KEY;

/**
 * <p>
 * 北向接入 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-02-07
 */
@Service
@CacheConfig(cacheNames = {CACHE_KEY})
public class NorthwardServiceImpl extends BaseServiceImpl<NorthwardMapper, Northward> implements INorthwardService {

    private final CacheManager cacheManager;
    final static String CACHE_KEY = "IBOOT:CACHE:NORTHWARD";

    public NorthwardServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public List<Northward> listByType(String type) {
        Cache cache = cacheManager.getCache(CACHE_KEY);
        Cache.ValueWrapper valueWrapper = cache.get(type);
        if(valueWrapper != null) {
            return (List<Northward>) valueWrapper.get();
        }

        List<Northward> northwards = this.list(Wrappers.<Northward>lambdaQuery().eq(Northward::getType, type)).getData();
        if(CollectionUtil.isNotEmpty(northwards)) {
            northwards = northwards.stream().filter(Northward::getStatus).collect(Collectors.toList());
            cache.put(type, northwards);
        } else {
            cache.put(type, northwards);
        }

        return northwards;
    }

    @Override
    public Result<Boolean> switchStatus(String connectKey, String status) {
        Northward northward = this.getByConnectKey(connectKey).ifNotPresentThrow("记录不存在").getData();
        if("online".equals(status)) {
            if(!northward.getStatus()) {
                return Result.fail("请先切换状态到启用");
            }
        }

        Object switchStatus = doSwitchStatus(northward, status);
        if(switchStatus instanceof ChannelFuture) {
            try {
                boolean await = ((ChannelFuture) switchStatus).await(5, TimeUnit.SECONDS);
                if(!await) {
                    return Result.fail("连接超时");
                }
            } catch (InterruptedException e) {
                return Result.fail(e.getMessage());
            }
        }

        return Result.success();
    }

    @Override
    public DetailResult<Northward> getByConnectKey(String connectKey) {
        return getOne(Wrappers.<Northward>lambdaQuery().eq(Northward::getConnectKey, connectKey));
    }

    private Object doSwitchStatus(Northward northward, String status) {
        boolean isOnline = "online".equals(status);
        FrameworkComponent component = null;
        ClientConnectProperties connectProperties = null;
        JSONObject config = northward.getConfig();
        if(northward.isMqtt()) {
            component = FrameworkManager.getComponent(MqttGatewayMessage.class);
            connectProperties = new MqttGatewayConnectProperties(config.getString("host"), config.getInteger("port")
                    , config.getString("clientId"), config.getString("topic"), MqttQoS.valueOf(config.getIntValue("qos")));
            ((MqttGatewayConnectProperties) connectProperties).setPassword(config.getString("password"));
            ((MqttGatewayConnectProperties) connectProperties).setUsername(config.getString("username"));
            connectProperties.setConnectKey(northward.getConnectKey());
        }

        if(component instanceof ClientComponent<?>) {
            IotClient client = ((ClientComponent<?>) component).getClient(connectProperties);
            if(isOnline) {
                if(client == null) {
                    client = ((ClientComponent<?>) component).createNewClient(connectProperties);
                    client.init(config);
                }

                return client.connect();
            } else {
                if(client != null) {
                    return client.close();
                }
            }
        }

        return null;
    }

    @Override
    public BooleanResult save(Northward entity) {
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Northward entity) {
        return super.updateById(entity).ifPresentAndTrue(item -> {
            try {
                doSwitchStatus(entity, "offline");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Cache cache = cacheManager.getCache(CACHE_KEY);
                cache.evict(entity.getType());
            }
        });
    }
}
