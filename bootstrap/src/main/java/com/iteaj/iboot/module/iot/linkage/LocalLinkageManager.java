package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalLinkageManager implements LinkageManager{

    private Map<String, Map<Long, LinkageItem>> items = new ConcurrentHashMap<>(1000);

    @Override
    public LinkageManager register(LinkageItem item) {
        return null;
    }

    @Override
    public LinkageManager remove(Long itemId) {
        return null;
    }

    @Override
    public List<LinkageItem> listLinkageItem(Long linkageId) {
        return null;
    }

    @Override
    public List<LinkageItem> listLinkageItem(String eventApiCode) {
        return null;
    }

    @Override
    public List<LinkageItem> listLinkageItem(String protocolCode, String direct, String attrField) {
        return null;
    }
}
