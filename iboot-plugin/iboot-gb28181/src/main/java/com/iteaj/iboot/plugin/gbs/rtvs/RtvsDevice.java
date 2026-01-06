package com.iteaj.iboot.plugin.gbs.rtvs;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import lombok.Data;

import java.util.Locale;

@Data
public class RtvsDevice {
    /**
     * 设备国标编号
     */
    @JSONField(name = "DeviceId")
    private String deviceId;

    /**
     * 设备名
     */
    @JSONField(name = "DeviceName")
    private String name;

    /**
     * 生产厂商
     */
    @JSONField(name = "Manufacturer")
    private String manufacturer;

    /**
     * 型号
     */
    @JSONField(name = "Model")
    private String model;

    /**
     * 固件版本
     */
    @JSONField(name = "Firmware")
    private String firmware;

    /**
     * wan地址
     */
    @JSONField(name = "RemoteInfo")
    private String remoteInfo;

    /**
     * 是否在线，true为在线，false为离线
     */
    @JSONField(name = "Online")
    private Boolean online;

    /**
     * 注册时间
     */
    @JSONField(name = "OnlineTime")
    private String registerTime;

    /**
     * 心跳时间
     */
    @JSONField(name = "KeepAliveTime")
    private String keepaliveTime;

    /**
     * 通道个数
     */
    @JSONField(name = "CatalogChannel")
    private Integer channelCount;

    /**
     * 注册有效期
     */
    @JSONField(name = "SubscribeExpires")
    private Integer expires;

    /**
     * 创建时间
     */
    @JSONField(name = "CreateTime")
    private String createTime;

    /**
     * 更新时间
     */
    @JSONField(name = "UpTime")
    private String updateTime;

    @JSONField(name = "NickName")
    private String nickName;

    @JSONField(name = "HasAlarm")
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
