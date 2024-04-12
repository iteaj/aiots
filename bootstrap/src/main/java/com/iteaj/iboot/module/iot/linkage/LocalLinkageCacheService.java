package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocalLinkageCacheService implements LinkageCacheService {

    private Map<String, Set<LinkageItem>> productFieldMap = new ConcurrentHashMap<>(1024);
    // eventSourceId -> LinkageId -> deviceGroupId
    private Map<Long, Map<Long, Map<Long, Set<LinkageItem>>>> eventSourceMap = new HashMap<>(36);

    @Override
    public synchronized void register(Long eventSourceId, List<LinkageItem> items) {
        Map<Long, Map<Long, Set<LinkageItem>>> sourceMap = eventSourceMap.get(eventSourceId);
        if(sourceMap == null) {
            eventSourceMap.put(eventSourceId, sourceMap = new HashMap<>());
        }
        for (int i = 0; i < items.size(); i++) {
            LinkageItem item = items.get(i);
            Map<Long, Set<LinkageItem>> linkageMap = sourceMap.get(item.getLinkageId());
            if(linkageMap == null) {
                sourceMap.put(item.getLinkageId(), linkageMap = new ConcurrentHashMap<>());
            }

            for (int j = 0; j < item.getDeviceGroupIds().size(); j++) {
                Long deviceGroupId = (Long) item.getDeviceGroupIds().get(j);
                Set<LinkageItem> linkageItems = linkageMap.get(deviceGroupId);
                if(linkageItems == null) {
                    linkageMap.put(deviceGroupId, linkageItems = new HashSet<>());
                }

                linkageItems.add(item);
            }

            item.getConditions().forEach(condition -> {
                condition.getItems().forEach(conditionItem -> {
                    String productField = conditionItem.getProductCode() + "->" + conditionItem.getTypeValue();
                    Set<LinkageItem> linkageItems = productFieldMap.get(productField);
                    if(linkageItems == null) {
                        productFieldMap.put(productField, linkageItems = new HashSet<>());
                    }

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
                    if(iterator.hasNext()) {
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
            Iterator<Map<Long, Map<Long, Set<LinkageItem>>>> iterator = values.iterator();
            if(iterator.hasNext()) {
                Map<Long, Map<Long, Set<LinkageItem>>> next = iterator.next();
                if(next.containsKey(linkageId)) {
                    iterator.remove(); return;
                }
            }
        }

        // 移除此联动场景下面的所有联动项
        productFieldMap.values().forEach(linkageItems -> {
            Iterator<LinkageItem> iterator = linkageItems.iterator();
            if(iterator.hasNext()) {
                LinkageItem next = iterator.next();
                if(next.getLinkageId().equals(linkageId)) {
                    iterator.remove();
                }
            }
        });
    }

    @Override
    public Set<LinkageItem> listLinkageItem(String productCode, String signalOrField) {
        return productFieldMap.get(productCode + "->" + signalOrField);
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
}
