package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;

public class LocalLinkageCacheService implements LinkageCacheService{

    @Override
    public void register(LinkageItem item) {

    }

    @Override
    public List<LinkageItem> getLinkageItems(Long eventSourceId, Long groupId) {
        return null;
    }

    @Override
    public void removeLinkageItemsByGroupId(Long groupId) {

    }

    @Override
    public void removeLinkageItemsByEventSourceId(Long eventSourceId) {

    }
}
