package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.iboot.module.iot.entity.LinkageItem;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocalLinkageCacheService implements LinkageCacheService, ApplicationReadyListener {

    private final Map<String, Set<LinkageItem>> productFieldMap = new ConcurrentHashMap<>(1024);
    // eventSourceId -> LinkageId -> deviceGroupId
    private final Map<Long, Map<Long, Map<Long, Set<LinkageItem>>>> eventSourceMap = new HashMap<>(36);

    @Override
    public synchronized void register(Long eventSourceId, List<LinkageItem> items) {
        Map<Long, Map<Long, Set<LinkageItem>>> sourceMap = eventSourceMap.computeIfAbsent(eventSourceId, k -> new HashMap<>());
        for (LinkageItem item : items) {
            Map<Long, Set<LinkageItem>> linkageMap = sourceMap.computeIfAbsent(item.getLinkageId(), k -> new ConcurrentHashMap<>());

            for (int j = 0; j < item.getDeviceGroupIds().size(); j++) {
                Long deviceGroupId = item.getDeviceGroupIds().getLong(j);
                Set<LinkageItem> linkageItems = linkageMap.computeIfAbsent(deviceGroupId, k -> new HashSet<>());

                linkageItems.add(item);
            }

            item.getConditions().forEach(condition -> {
                condition.getItems().forEach(conditionItem -> {
                    String productField = conditionItem.getProductCode() + "->" + conditionItem.getTypeValue();
                    Set<LinkageItem> linkageItems = productFieldMap.computeIfAbsent(productField, k -> new HashSet<>());

                    linkageItems.add(item);
                });
            });
        }
    }

    @Override
    public synchronized void removeLinkageItemsByGroupId(Long groupId) {
        // linkageId -> deviceGroupId
        Collection<Map<Long, Map<Long, Set<LinkageItem>>>> values = eventSourceMap.values();
        if(CollectionUtil.isNotEmpty(values)) {
            values.forEach(item -> {
                Collection<Map<Long, Set<LinkageItem>>> groupMap = item.values();
                if(groupMap != null) {
                    Iterator<Map<Long, Set<LinkageItem>>> iterator = groupMap.iterator();
                    while (iterator.hasNext()) {
                        Map<Long, Set<LinkageItem>> next = iterator.next();
                        if(next.containsKey(groupId)) {
                            iterator.remove(); return;
                        }
                    }
                }
            });
        }
    }

    @Override
    public synchronized void removeLinkageItemsByLinkageId(Long linkageId) {
        Collection<Map<Long, Map<Long, Set<LinkageItem>>>> values = eventSourceMap.values();
        if(CollectionUtil.isNotEmpty(values)) {
            values.removeIf(next -> next.containsKey(linkageId));
        }

        // 移除此联动场景下面的所有联动项
        productFieldMap.values().forEach(linkageItems -> {
            linkageItems.removeIf(next -> next.getLinkageId().equals(linkageId));
        });
    }

    @Override
    public Set<LinkageItem> listLinkageItem(String productCode, String signalOrField) {
        Set<LinkageItem> linkageItems = productFieldMap.get(productCode + "->" + signalOrField);
        return linkageItems != null ? linkageItems : Collections.emptySet();
    }

    @Override
    public Set<LinkageItem> listLinkageItem(Long eventSourceId, Long groupId) {
        Set<LinkageItem> items = new HashSet<>();
        Map<Long, Map<Long, Set<LinkageItem>>> linkageMap = eventSourceMap.get(eventSourceId);
        if(linkageMap != null) {
            linkageMap.values().forEach(groupMap -> {
                Set<LinkageItem> linkageItems = groupMap.get(groupId);
                if(linkageItems != null) {
                    items.addAll(linkageItems);
                }
            });
        }

        return items;
    }

    @Override
    public void started(ApplicationContext context) {
        ILinkageService linkageService = context.getBean(ILinkageService.class);
        linkageService.listDetailExecOfEnabled().forEach(linkage -> {
            if(CollectionUtil.isNotEmpty(linkage.getItems())) {
                linkage.getItems().forEach(item -> {
                    if(item.useAllGroup()) { // 使用和事件源相同的所有组
                        item.setDeviceGroupIds(linkage.getSourceDeviceGroupIds());
                    }
                });

                this.register(linkage.getEventSourceId(), linkage.getItems());
            } else {
                throw new ServiceException("没有配置联动项");
            }
        });
    }
}
