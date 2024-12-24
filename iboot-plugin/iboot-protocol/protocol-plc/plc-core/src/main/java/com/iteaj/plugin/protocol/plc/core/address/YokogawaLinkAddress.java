package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

public class YokogawaLinkAddress extends DeviceAddressDataBase {

    /**
     * 获取等待读取的数据的代码
     * Get or set the code of the data waiting to be read
     * @return 数据代码
     */
    public int getDataCode() {
        return DataCode;
    }

    /**
     * 设置读取的代码数据
     * @param dataCode 地址代码
     */
    public void setDataCode(int dataCode) {
        DataCode = dataCode;
    }

    private int DataCode = 0; // 获取或设置等待读取的数据的代码

    /**
     * 获取当前横河PLC的地址的二进制表述方式<br />
     * Obtain the binary representation of the current Yokogawa PLC address
     * @return 二进制数据信息
     */
    public byte[] GetAddressBinaryContent( ) {
        byte[] buffer = new byte[6];
        buffer[0] = Utilities.getBytes(DataCode)[1];
        buffer[1] = Utilities.getBytes(DataCode)[0];
        buffer[2] = Utilities.getBytes(getAddressStart())[3];
        buffer[3] = Utilities.getBytes(getAddressStart())[2];
        buffer[4] = Utilities.getBytes(getAddressStart())[1];
        buffer[5] = Utilities.getBytes(getAddressStart())[0];

        return buffer;
    }

    public void Parse( String address, short length )
    {
        OperateResultExOne<YokogawaLinkAddress> addressData = ParseFrom( address, length );
        if (addressData.IsSuccess)
        {
            setAddressStart(addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            DataCode = addressData.Content.DataCode;
        }
    }

    public String toString( )
    {
        switch (DataCode)
        {
            case 0x31: return "CN" + getAddressStart();
            case 0x21: return "TN" + getAddressStart();
            case 0x18: return "X"  + getAddressStart();
            case 0x19: return "Y"  + getAddressStart();
            case 0x09: return "I"  + getAddressStart();
            case 0x05: return "E"  + getAddressStart();
            case 0x0D: return "M"  + getAddressStart();
            case 0x14: return "T"  + getAddressStart();
            case 0x03: return "C"  + getAddressStart();
            case 0x0C: return "L"  + getAddressStart();
            case 0x04: return "D"  + getAddressStart();
            case 0x02: return "B"  + getAddressStart();
            case 0x06: return "F"  + getAddressStart();
            case 0x12: return "R"  + getAddressStart();
            case 0x16: return "V"  + getAddressStart();
            case 0x1A: return "Z"  + getAddressStart();
            case 0x17: return "W"  + getAddressStart();
            default: return String.valueOf(getAddressStart());
        }
    }

    /**
     * 从普通的PLC的地址转换为标准的地址信息
     * @param address 地址信息
     * @param length 数据长度
     * @return 是否成功的地址结果
     */
    public static OperateResultExOne<YokogawaLinkAddress> ParseFrom( String address, short length )
    {
        try
        {
            int type = 0;
            int offset = 0;
            if      (address.startsWith( "CN" ) || address.startsWith( "cn" )) { type = 0x31; offset = Integer.parseInt( address.substring( 2 ) ); }
            else if (address.startsWith( "TN" ) || address.startsWith( "tn" )) { type = 0x21; offset = Integer.parseInt( address.substring( 2 ) ); }
            else if (address.startsWith( "X" )  || address.startsWith( "x" ))  { type = 0x18; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "Y" )  || address.startsWith( "y" ))  { type = 0x19; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "I" )  || address.startsWith( "i" ))  { type = 0x09; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "E" )  || address.startsWith( "e" ))  { type = 0x05; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "M" )  || address.startsWith( "m" ))  { type = 0x0D; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "T" )  || address.startsWith( "t" ))  { type = 0x14; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "C" )  || address.startsWith( "c" ))  { type = 0x03; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "L" )  || address.startsWith( "l" ))  { type = 0x0C; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "D" )  || address.startsWith( "d" ))  { type = 0x04; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "B" )  || address.startsWith( "b" ))  { type = 0x02; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "F" )  || address.startsWith( "f" ))  { type = 0x06; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "R" )  || address.startsWith( "r" ))  { type = 0x12; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "V" )  || address.startsWith( "v" ))  { type = 0x16; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "Z" )  || address.startsWith( "z" ))  { type = 0x1A; offset = Integer.parseInt( address.substring( 1 ) ); }
            else if (address.startsWith( "W" )  || address.startsWith( "w" ))  { type = 0x17; offset = Integer.parseInt( address.substring( 1 ) ); }
            else { throw new Exception( StringResources.Language.NotSupportedDataType() ); }

            YokogawaLinkAddress yokogawaLinkAddress = new YokogawaLinkAddress();
            yokogawaLinkAddress.setAddressStart(offset);
            yokogawaLinkAddress.setLength(length);
            yokogawaLinkAddress.setDataCode(type);
            return OperateResultExOne.CreateSuccessResult( yokogawaLinkAddress);
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<YokogawaLinkAddress>( ex.getMessage() );
        }
    }
}
