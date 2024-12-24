package com.iteaj.plugin.protocol.plc.core.profinet.Inovance;

import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.modbus.IModbus;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusRtuOverTcp;

/**
 * 汇川的串口转网口通信协议，A适用于AM400、 AM400_800、 AC800、H3U, XP, H5U 等系列底层走的是MODBUS-RTU协议，地址说明参见标记<br />
 * Inovance's serial communication protocol is applicable to AM400, AM400_800, AC800 and other series. The bottom layer is MODBUS-RTU protocol. For the address description, please refer to the mark
 * <br /><br />
 * AM400_800 的元件有 Q 区，I 区，M 区这三种，分别都可以按位，按字节，按字和按双字进行访问，在本组件的条件下，仅支持按照位，字访问。<br />
 * 位地址支持 Q, I, M 地址类型，字地址支持 SM, SD，支持对字地址的位访问，例如 ReadBool("SD0.5");<br />
 * H3U 系列控制器支持 M/SM/S/T/C/X/Y 等 bit 型变量（也称线圈） 的访问、 D/SD/R/T/C 等 word 型变量的访问；<br />
 * H5U 系列控制器支持 M/B/S/X/Y 等 bit 型变量（也称线圈） 的访问、 D/R 等 word 型变量的访问；内部 W 元件，不支持通信访问。<br />
 */
public class InovanceSerialOverTcp extends ModbusRtuOverTcp {

    // region Constructor

    /**
     * 实例化一个默认的对象
     */
    public InovanceSerialOverTcp( ) {
        super();
        Series = InovanceSeries.AM;
        getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    /**
     * 通过指定站号，ip地址，端口号来实例化一个新的对象
     * @param ipAddress Ip地址
     * @param port 端口号
     * @param station 站号信息
     */
    public InovanceSerialOverTcp( String ipAddress, int port, byte station )
    {
        super(ipAddress, port, station);
        Series = InovanceSeries.AM;
        getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    /**
     * 通过指定站号，IP地址，端口以及PLC的系列来实例化一个新的对象<br />
     * @param series PLC的系列
     * @param ipAddress Ip地址
     * @param port 端口号
     * @param station 站号信息
     */
    public InovanceSerialOverTcp( InovanceSeries series, String ipAddress, int port, byte station )
    {
        super(ipAddress, port, station);
        Series = series;
    }

    // endregion

    // region Public Properties

    /**
     * 获取当前PLC的系列信息
     * @return 系列
     */
    public InovanceSeries getSeries() {
        return Series;
    }

    /**
     * 设置当前PLC的系列信息，需要在连接之前设置完成
     * @param series 系列信息
     */
    public void setSeries(InovanceSeries series) {
        Series = series;
    }

    private InovanceSeries Series = InovanceSeries.AM;   // 系列信息

    // endregion

    // region Override

    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode )
    {
        return InovanceHelper.PraseInovanceAddress( this.Series, address, modbusCode );
    }

    // endregion

    // region Read Write Byte

    /**
     * @see InovanceHelper#ReadByte(IModbus, String)
     */
    public OperateResultExOne<Byte> ReadByte( String address ) {
        return InovanceHelper.ReadByte( this, address );
    } 

    // endregion

    // region Object Override

    public String toString( ) {
        return  "InovanceSerialOverTcp<" + Series + ">[" + getIpAddress() + ":" + getPort() + "]";
    }

    // endregion
}
