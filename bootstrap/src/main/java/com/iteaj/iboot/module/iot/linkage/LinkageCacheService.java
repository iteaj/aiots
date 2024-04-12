package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;
import java.util.Set;

public interface LinkageCacheService {

    /**
     * 注册联动项
     * @param items
     */
    void register(Long eventSourceId, List<LinkageItem> items);

    /**
     * 移除指定组下面的所有联动项
     * @param groupId
     */
    void removeLinkageItemsByGroupId(Long groupId);

    /**
     * 移除指定场景联动下的所有联动项
     * @param linkageId
     */
    void removeLinkageItemsByLinkageId(Long linkageId);

    /**
     * 获取指定产品下面指定属性的所有联动项
     * @param productCode
     * @param signalOrField
     * @return
     */
    Set<LinkageItem> listLinkageItem(String productCode, String signalOrField);

    /**
     * 获取指定事件源和组下的联动项列表
     * @param eventSourceId
     * @param groupId
     * @return
     */
    Set<LinkageItem> listLinkageItem(Long eventSourceId, Long groupId);
}
