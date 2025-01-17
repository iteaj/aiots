package com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.Encoding;

/**
 * Ads设备的相关信息，主要是版本号，设备名称<br />
 * Information about Ads devices, primarily version numbers, device names.
 */
public class AdsDeviceInfo {
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public AdsDeviceInfo( )
    {

    }

    /**
     * 根据原始的数据内容来实例化一个对象<br />
     * Instantiate an object based on the original data content
     * @param data 原始的数据内容
     */
    public AdsDeviceInfo( byte[] data )
    {
        Major = data[0] & 0xff;
        Minor = data[1] & 0xff;
        Build = BitConverter.ToUInt16( data, 2 );
        DeviceName = Encoding.ASCII.GetString( SoftBasic.BytesArrayRemoveBegin(data, 4 ) ).trim( );
    }

    /**
     * 主版本号<br />
     * Main Version
     */
    public int Major = 0;

    /**
     * 次版本号<br />
     * Minor Version
     */
    public int Minor = 0;

    /**
     * 构建版本号<br />
     * Build version
     */
    public int Build = 0;

    /**
     * 设备的名字<br />
     * Device Name
     */
    public String DeviceName = "";
}
