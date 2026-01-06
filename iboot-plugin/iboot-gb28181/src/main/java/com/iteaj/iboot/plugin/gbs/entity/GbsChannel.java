package com.iteaj.iboot.plugin.gbs.entity;

import cn.hutool.core.bean.BeanUtil;
import com.iteaj.iboot.plugin.gbs.rtvs.RtvsChannel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class GbsChannel {
    /**
     * 通道国标编号
     */
    private String channelId;

    /**
     * 设备国标编号
     */
    private String deviceId;

    /**
     * 通道名
     */
    private String name;

    /**
     * 通道别名
     */
    private String nickName;

    /**
     * 生产厂商
     */
    private String manufacture;

    /**
     * 型号
     */
    private String model;

    /**
     * 设备归属
     */
    private String owner;

    /**
     * 行政区域
     */
    private String civilCode;

    /**
     * 警区
     */
    private String block;

    /**
     * 安装地址
     */
    private String address;

    /**
     * 是否有子设备 1有, 0没有
     */
    private Integer parental;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 信令安全模式  缺省为0; 0:不采用; 2: S/MIME签名方式; 3: S/ MIME加密签名同时采用方式; 4:数字摘要方式
     */
    private Integer safetyWay;

    /**
     * 注册方式 缺省为1;1:符合IETFRFC3261标准的认证注册模 式; 2:基于口令的双向认证注册模式; 3:基于数字证书的双向认证注册模式
     */
    private Integer registerWay;

    /**
     * 证书序列号
     */
    private String certNum;

    /**
     * 证书有效标识 缺省为0;证书有效标识:0:无效1: 有效
     */
    private Integer certifiable;

    /**
     * 证书无效原因码
     */
    private Integer errCode;

    /**
     * 证书终止有效期
     */
    private String endTime;

    /**
     * 保密属性 缺省为false
     * false:不涉密, true:涉密
     */
    private Boolean secrecy;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 密码
     */
    private String password;


    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
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
    private Boolean status;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 子设备数
     */
    private Integer subCount;

    /**
     * 流唯一编号，存在表示正在直播
     */
    private String  streamId;

    /**
     *  是否含有音频
     */
    private Boolean hasAudio;

    /**
     * 标记通道的类型，0->国标通道 1->直播流通道 2->业务分组/虚拟组织/行政区划
     */
    private Integer channelType;

    /**
     * 业务分组
     */
    private String businessGroupId;

    /**
     * GPS的更新时间
     */
    private String gpsTime;

    public RtvsChannel toRtvsChannel() {
        RtvsChannel channel = new RtvsChannel();
        BeanUtil.copyProperties(this, channel);
        return channel;
    }
}
