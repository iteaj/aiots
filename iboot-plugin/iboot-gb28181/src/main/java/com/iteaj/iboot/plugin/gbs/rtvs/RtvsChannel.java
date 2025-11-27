package com.iteaj.iboot.plugin.gbs.rtvs;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class RtvsChannel {
    /**
     * 通道国标编号
     */
    @JSONField(name="ChannelID")
    private String channelId;

    /**
     * 设备国标编号
     */
    @JSONField(name="DeviceID")
    private String deviceId;

    /**
     * 通道名
     */
    @JSONField(name="Name")
    private String name;

    /**
     * 别名
     */
    @JSONField(name="NickName")
    private String nickName;

    /**
     * 生产厂商
     */
    @JSONField(name="Manufacturer")
    private String manufacture;

    /**
     * 型号
     */
    @JSONField(name="Model")
    private String model;

    /**
     * 设备归属
     */
    @JSONField(name="Owner")
    private String owner;

    /**
     * 行政区域
     */
    @JSONField(name="CivilCode")
    private String civilCode;

    /**
     * 警区
     */
    @JSONField(name="Block")
    private String block;

    /**
     * 安装地址
     */
    @JSONField(name="Address")
    private String address;

    /**
     * 是否有子设备 1有, 0没有
     */
    @JSONField(name="Parental")
    private Integer parental;

    /**
     * 父级id
     */
    @JSONField(name="ParentID")
    private String parentId;

    /**
     * 信令安全模式  缺省为0; 0:不采用; 2: S/MIME签名方式; 3: S/ MIME加密签名同时采用方式; 4:数字摘要方式
     */
    @JSONField(name="SafetyWay")
    private Integer safetyWay;

    /**
     * 注册方式 缺省为1;1:符合IETFRFC3261标准的认证注册模 式; 2:基于口令的双向认证注册模式; 3:基于数字证书的双向认证注册模式
     */
    @JSONField(name="RegisterWay")
    private Integer registerWay;

    /**
     * 证书序列号
     */
    @JSONField(name="CertNum")
    private String certNum;

    /**
     * 证书有效标识 缺省为0;证书有效标识:0:无效1: 有效
     */
    @JSONField(name="Certifiable")
    private Integer certifiable;

    /**
     * 证书无效原因码
     */
    @JSONField(name="ErrCode")
    private Integer errCode;

    /**
     * 证书终止有效期
     */
    @JSONField(name="EndTime")
    private String endTime;

    /**
     * 保密属性 缺省为false; false:不涉密, true:涉密
     */
    @JSONField(name="Secrecy")
    private Boolean secrecy;

    /**
     * IP地址
     */
    @JSONField(name="IPAddress")
    private String ipAddress;

    /**
     * 端口号
     */
    @JSONField(name="Port")
    private Integer port;

    /**
     * 密码
     */
    @JSONField(name="Password")
    private String password;

    /**
     * 更新时间
     */
    @JSONField(name="OfflineTime")
    private String updateTime;

    /**
     * 在线/离线
     * 1在线,0离线
     * 默认在线
     * 信令:
     * <Status>ON</Status>
     * <Status>OFF</Status>
     * 遇到过NVR下的IPC下发信令可以推流， 但是 Status 响应 OFF
     */
    @JSONField(name="Online")
    private Boolean status;

    /**
     * 经度
     */
    @JSONField(name="Longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @JSONField(name="Latitude")
    private Double latitude;

    /**
     * 业务分组
     */
    @JSONField(name="BusinessGroupID")
    private String businessGroupId;

    public GbsChannel convert() {
        GbsChannel gbsChannel = new GbsChannel();
        BeanUtil.copyProperties(this, gbsChannel);
        return gbsChannel;
    }
}
