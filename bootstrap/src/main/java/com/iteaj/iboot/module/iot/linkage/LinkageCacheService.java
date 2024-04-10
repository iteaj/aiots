package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;

public interface LinkageCacheService {

    /**
     * 注册联动项
     * @param item
     */
    void register(LinkageItem item);

    /**
     * 获取指定事件源和组下的联动项列表
     * @param eventSourceId
     * @param groupId
     * @return
     */
    List<LinkageItem> getLinkageItems(Long eventSourceId, Long groupId);

    /**
     * 移除指定组下面的所有联动项
     * @param groupId
     */
    void removeLinkageItemsByGroupId(Long groupId);

    /**
     * 移除指定事件源下面的所有联动项
     * @param eventSourceId
     */
    void removeLinkageItemsByEventSourceId(Long eventSourceId);
}
