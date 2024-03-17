package com.iteaj.iboot.module.iot.linkage;

import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;

public interface LinkageManager {

    /**
     * 获取指定场景下的联动项列表
     * @param linkageId
     * @return
     */
    List<LinkageItem> listLinkageItem(Long linkageId);


}
