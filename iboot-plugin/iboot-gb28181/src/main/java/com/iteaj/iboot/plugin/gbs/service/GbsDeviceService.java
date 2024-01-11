package com.iteaj.iboot.plugin.gbs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;

public interface GbsDeviceService {

    /**
     * 获取国标设备通过deviceId
     * @param deviceId
     * @return
     */
    GbsDevice getByDeviceId(String deviceId);

    /**
     * 删除国标设备通过deviceId
     * @param deviceId
     * @return
     */
    boolean deleteByDeviceId(String deviceId);

    /**
     * 分页获取设备列表
     * @param page
     * @param entity
     * @return
     */
    Page<GbsDevice> page(Page page, GbsDevice entity);

    /**
     * 同步设备通道
     * @param deviceId
     */
    void sync(String deviceId);

    /**
     * 更新设备
     * @param device
     * @return
     */
    boolean update(GbsDevice device);

    /**
     * 获取通道列表
     * @param page
     * @param entity
     * @return
     */
    Page<GbsChannel> channels(Page page, GbsChannel entity);

    /**
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param command 控制指令,允许值: left, right, up, down, upleft, upright, downleft, downright, zoomin, zoomout, stop
     * @param horizonSpeed 水平速度
     * @param verticalSpeed 垂直速度
     * @param zoomSpeed 缩放速度
     * @return
     */
    void ptzCtrl(String deviceId, String channelId, String command, int horizonSpeed, int verticalSpeed, int zoomSpeed);
}
