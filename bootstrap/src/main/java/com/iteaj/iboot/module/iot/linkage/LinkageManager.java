package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;

public interface LinkageManager {

    /**
     * 注册联动项
     * @param item
     * @return
     */
    LinkageManager register(LinkageItem item);

    /**
     * 移除联动项
     * @param itemId
     * @return
     */
    LinkageManager remove(Long itemId);

    /**
     * 获取指定场景下的联动项列表
     * @param linkageId
     * @return
     */
    List<LinkageItem> listLinkageItem(Long linkageId);

    /**
     * 通过事件接口码获取联动项
     * @param eventApiCode
     * @return
     */
    List<LinkageItem> listLinkageItem(String eventApiCode);

    /**
     * 通过协议码、协议接口码、协议接口属性
     * @param protocolCode 协议码
     * @param direct 协议指令
     * @param attrField 协议接口属性
     * @return
     */
    List<LinkageItem> listLinkageItem(String protocolCode, String direct, String attrField);
}
