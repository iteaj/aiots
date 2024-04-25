package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VlsDeviceAndProductDto {

    /**
     * 在线数量
     */
    private int onlineNum;

    /**
     * 离线数量
     */
    private int offlineNum;

    /**
     * 总产品数量
     */
    private int totalProductNum;

    /**
     * 启用的产品数量
     */
    private int enabledProductNum;

    /**
     * 总事件源
     */
    private int totalEventSourceNum;

    /**
     * 运行中的事件源
     */
    private int runningEventSourceNum;

    /**
     * 总的事件联动项
     */
    private int totalLinkageNum;

    /**
     * 运行中的联动项
     */
    private int runningLinkageNum;
}
