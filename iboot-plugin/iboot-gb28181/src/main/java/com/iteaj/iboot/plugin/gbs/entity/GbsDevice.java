package com.iteaj.iboot.plugin.gbs.entity;


import cn.hutool.core.bean.BeanUtil;
//import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.iteaj.framework.Entity;
import com.iteaj.iboot.plugin.gbs.rtvs.RtvsDevice;
import lombok.Data;

/**
 * 国标设备/平台
 */
@Data
public class GbsDevice implements Entity<Long> {

	private Long id;

	/**
	 * 设备国标编号
	 */
	private String deviceId;

	/**
	 * 设备名
	 */
	private String name;

	/**
	 * 设备别名
	 */
	private String nickName;

	/**
	 * 生产厂商
	 */
	private String manufacturer;

	/**
	 * 型号
	 */
	private String model;

	/**
	 * 固件版本
	 */
	private String firmware;

	/**
	 * 传输协议
	 * UDP/TCP
	 */
	private String transport;

	/**
	 * ip
	 */
	private String  ip;

	/**
	 * port
	 */
	private Integer port;

	/**
	 * wan地址
	 */
	private String hostAddress;

	/**
	 * 是否在线，true为在线，false为离线
	 */
	private Boolean online;

	/**
	 * 注册时间
	 */
	private String registerTime;

	/**
	 * 心跳时间
	 */
	private String keepaliveTime;


	/**
	 * 心跳间隔
	 */
	private Integer keepaliveIntervalTime;

	/**
	 * 通道个数
	 */
	private Integer channelCount;

	/**
	 * 注册有效期
	 */
	private Integer expires;

	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 更新时间
	 */
	private String updateTime;

	/**
	 * 字符集, 支持 UTF-8 与 GB2312
	 */
	private String charset ;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 流媒体类型(rtvs, wvp)
	 */
	private String media;

	public static final String MEDIA_WVP = "wvp";
	public static final String MEDIA_RTVS = "rtvs";

	/**
	 * 转到rtvs设备对象
	 * @return
	 */
	public RtvsDevice toRtvsDevice() {
		this.media = MEDIA_RTVS;
		RtvsDevice rtvsDevice = new RtvsDevice();
		BeanUtil.copyProperties(this, rtvsDevice);
		return rtvsDevice;
	}

	/**
	 * 转到Wvp设备对象
	 * @return
	 */
//	public Device toWvpDevice() {
//		this.media = MEDIA_WVP;
//		Device device = new Device();
//		BeanUtil.copyProperties(this, device);
//		return device;
//	}
}
