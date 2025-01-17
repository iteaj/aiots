package com.iteaj.plugin.protocol.plc.core.profinet.XINJE;

import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusRtuOverTcp;

/**
 * 信捷PLC的XC,XD,XL系列的串口转网口通讯类，虽然硬件层走的是TCP协议，但是底层使用ModbusRtu协议实现，每个系列支持的地址类型及范围不一样，详细参考API文档<br />
 * Xinje PLC's XC, XD, XL series serial port to network port communication type, although the hardware layer uses TCP protocol,
 * but the bottom layer is implemented by ModbusRtu protocol. The address types and ranges supported by each series are different.
 * Please refer to the API documentation for details.
 * <br /><br />
 * 对于XC系列适用于XC1/XC2/XC3/XC5/XCM/XCC系列，线圈支持X,Y,S,M,T,C，寄存器支持D,F,E,T,C<br />
 * 对于XD,XL系列适用于XD1/XD2/XD3/XD5/XDM/XDC/XD5E/XDME/XDH/XL1/XL3/XL5/XL5E/XLME，
 * 线圈支持X,Y,S,M,SM,T,C,ET,SEM,HM,HS,HT,HC,HSC 寄存器支持D,ID,QD,SD,TD,CD,ETD,HD,HSD,HTD,HCD,HSCD,FD,SFD,FS<br />
 */
public class XinJESerialOverTcp extends ModbusRtuOverTcp {
    /**
     * 实例化一个默认的对象
     */
    public XinJESerialOverTcp( )  {
        Series = XinJESeries.XC;
    }

    /**
     * 通过指定站号，ip地址，端口号来实例化一个新的对象
     * @param ipAddress Ip地址
     * @param port 端口号
     * @param station 站号信息
     */
    public XinJESerialOverTcp( String ipAddress, int port, byte station ) {
        super(ipAddress, port, station);
        Series = XinJESeries.XC;
    }

    /**
     * 通过指定站号，IP地址，端口以及PLC的系列来实例化一个新的对象<br />
     * Instantiate a new object by specifying the station number and PLC series
     * @param series PLC的系列
     * @param ipAddress Ip地址
     * @param port 端口号
     * @param station 站号信息
     */
    public XinJESerialOverTcp( XinJESeries series, String ipAddress, int port, byte station ) {
        super(ipAddress, port, station);
        Series = series;
    }

    /**
     * 获取或设置当前的信捷PLC的系列，默认XC系列
     */
    public XinJESeries Series = XinJESeries.XC;


    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode ) {
        return XinJEHelper.PraseXinJEAddress(this.Series, address, modbusCode);
    }

    public String toString( ) {
        return "XinJESerialOverTcp<" + Series + ">[" + getIpAddress() + ":" + getPort() + "]";
    }

}
