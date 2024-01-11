package com.iteaj.iboot.plugin.gbs.rtvs;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import lombok.Data;

import java.util.Locale;

@Data
@TableName("t_deviceinfo")
public class RtvsDevice {
    /**
     * 设备国标编号
     */
    @TableField("DeviceId")
    private String deviceId;

    /**
     * 设备名
     */
    @TableField("DeviceName")
    private String name;

    /**
     * 生产厂商
     */
    @TableField("Manufacturer")
    private String manufacturer;

    /**
     * 型号
     */
    @TableField("Model")
    private String model;

    /**
     * 固件版本
     */
    @TableField("Firmware")
    private String firmware;

    /**
     * wan地址
     */
    @TableField("RemoteInfo")
    private String remoteInfo;

    /**
     * 是否在线，true为在线，false为离线
     */
    @TableField("Online")
    private Boolean online;

    /**
     * 注册时间
     */
    @TableField("OnlineTime")
    private String registerTime;

    /**
     * 心跳时间
     */
    @TableField("KeepAliveTime")
    private String keepaliveTime;

    /**
     * 通道个数
     */
    @TableField("CatalogChannel")
    private Integer channelCount;

    /**
     * 注册有效期
     */
    @TableField("SubscribeExpires")
    private Integer expires;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private String createTime;

    /**
     * 更新时间
     */
    @TableField("UpTime")
    private String updateTime;

    @TableField("NickName")
    private String nickName;

    @TableField("HasAlarm")
    private Boolean hasAlarm;

    public GbsDevice convert() {
        GbsDevice device = new GbsDevice();
        BeanUtil.copyProperties(this, device);
        if(StrUtil.isNotBlank(this.remoteInfo)) {
            String[] split = this.remoteInfo.split(":");
            device.setIp(split[1]);
            device.setPort(Integer.valueOf(split[2]));
            device.setTransport(split[0].toUpperCase(Locale.ROOT));
        }

        device.setMedia(GbsDevice.MEDIA_RTVS);
        return device;
    }
}
