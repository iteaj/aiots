package com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff;

/**
 * 倍福PLC的命令码<br />
 * Command Id: https://infosys.beckhoff.com/english.php?content=../content/1033/tc3_ads_intro/index.html
 */
public class BeckhoffCommandId {
    /**
     * 读取PLC的名字和版本号等信息<br />
     * Reads the name and the version number of the ADS device.
     */
    public final static int ReadDeviceInfo = 0x0001;

    /**
     * 可以读ADS的设备读取数据<br />
     * With ADS Read data can be read from an ADS device
     */
    public final static int Read = 0x0002;

    /**
     * 将ADS数据写入到ADS的设备里去<br />
     * With ADS Write data can be written to an ADS device
     */
    public final static int Write = 0x0003;

    /**读取ADS设备里的设备状态信息和ADS状态<br />
     * Reads the ADS status and the device status of an ADS device.
     */
    public final static int ReadState = 0x0004;

    /**
     * 更改ADS设备的设备状态信息和ADS状态<br />
     * Changes the ADS status and the device status of an ADS device.
     */
    public final static int WriteControl = 0x0005;

    /**
     * 在ADS设备里面创建一个通知对象<br />
     * A notification is created in an ADS device
     */
    public final static int AddDeviceNotification = 0x0006;

    /**
     * 删除ADS设备里的一个通知对象<br />
     * One before defined notification is deleted in an ADS device.
     */
    public final static int DeleteDeviceNotification = 0x0007;

    /**
     * 从ADS设备订阅一个数据的通知，将会发送到客户端<br />
     * Data will carry forward independently from an ADS device to a Client
     */
    public final static int DeviceNotification = 0x0008;

    /**
     * 在写入的时候进行同时的读取<br />
     * With ADS ReadWrite data will be written to an ADS device. Additionally, data can be read from the ADS device.
     */
    public final static int ReadWrite = 0x0009;
}
