package com.iteaj.framework.spi.message;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务管理
 */
public class MessageManager {

    private Map<String, MessageService> serviceMap = new HashMap<>(16);

    public MessageManager(List<MessageService> services) {
        if(CollectionUtil.isNotEmpty(services)) {
            services.forEach(item -> this.register(item));
        }
    }

    /**
     * 注册服务
     * @param service
     * @return 已经存在返回 {@code null} 否则返回注册的对象
     */
    public MessageService register(MessageService service) {
        if(service == null) {
            throw new IllegalArgumentException("参数[service]不能为空");
        }

        if(StrUtil.isBlank(service.getType()) || StrUtil.isBlank(service.getChannelId())) {
            throw new IllegalArgumentException("参数[type] or [channelId]不能为空");
        }

        String key = service.getType() + ":" + service.getChannelId();

        if(serviceMap.containsKey(key)) {
            return null;
        } else {
            return serviceMap.put(key, service);
        }
    }

    /**
     * 是否存在指定服务
     * @param type 消息类型
     * @param channelId 通道标识
     * @return
     */
    public boolean contains(String type, String channelId) {
        return serviceMap.containsKey(type + ":" + channelId);
    }

    /**
     * 获取指定服务
     * @param type
     * @param channelId
     * @return
     */
    public MessageService getService(String type, String channelId) {
        return serviceMap.get(type + ":" + channelId);
    }

    /**
     * 移除某个服务
     * @param type
     * @param channelId
     * @return
     */
    public MessageService remove(String type, String channelId) {
        return serviceMap.remove(type + ":" + channelId);
    }

    /**
     * 获取类型列表
     * @return
     */
    public List<String> getTypes() {
        return this.serviceMap.values()
                .stream()
                .map(item -> item.getType())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 返回指定类型下的所有服务
     * @param type
     * @return
     */
    public List<MessageService> getServices(String type) {
        return this.serviceMap.entrySet().stream()
                .filter(item -> item.getKey().startsWith(type))
                .map(item -> item.getValue())
                .collect(Collectors.toList());
    }

    /**
     * 消息服务列表
     * @return
     */
    public Collection<MessageService> getServices() {
        return Collections.unmodifiableCollection(this.serviceMap.values());
    }
}
