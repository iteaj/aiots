package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;

public class ToyoPucAddress extends DeviceAddressDataBase {
    /**
     * 获取或设置等待读取的地址的PRG编号，如果小于0，则表示不使用。<br />
     * Gets or sets the PRG number of the address waiting to be read, if less than 0, it means that it is not used.
     */
    public int PRG = -1;

    public String toString( ) {
        return String.valueOf(getAddressStart());
    }

//region Static Method

    /// <summary>
/// 从实际的丰田PLC的地址里面解析出地址对象<br />
/// Resolve the address object from the actual Vigor address
/// </summary>
/// <param name="address">丰田的地址数据信息</param>
/// <param name="length">读取的数据长度</param>
/// <param name="isBit">是否是对位进行访问的</param>
/// <returns>是否成功的结果对象</returns>
    public static OperateResultExOne<ToyoPucAddress> ParseFrom(String address, short length, boolean isBit )
    {
        ToyoPucAddress addressData = new ToyoPucAddress( );
        addressData.setLength( length );
        OperateResultExTwo<Integer,String> extra = PlcHelper.ExtractParameter(address, "prg", -1);
        if (extra.IsSuccess){
            addressData.PRG = extra.Content1;
            address = extra.Content2;
        }
        try
        {
            if (address.charAt(0) == 'K' || address.charAt(0) == 'k')      addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x0200 : 0x020 ));
            else if (address.charAt(0) == 'V' || address.charAt(0) == 'v') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x0500 : 0x050 ));
            else if (address.charAt(0) == 'T' || address.charAt(0) == 't') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x0600 : 0x060 ));
            else if (address.charAt(0) == 'C' || address.charAt(0) == 'c') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x0600 : 0x060 ));
            else if (address.charAt(0) == 'L' || address.charAt(0) == 'l') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x0800 : 0x080 ));
            else if (address.charAt(0) == 'X' || address.charAt(0) == 'x') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x1000 : 0x100 ));
            else if (address.charAt(0) == 'Y' || address.charAt(0) == 'y') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x1000 : 0x100 ));
            else if (address.charAt(0) == 'M' || address.charAt(0) == 'm') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + ( isBit ? 0x1800 : 0x180 ));
                // 字读写的区域
            else if (address.charAt(0) == 'S' || address.charAt(0) == 's') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + 0x0200);
            else if (address.charAt(0) == 'N' || address.charAt(0) == 'n') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + 0x0600);
            else if (address.charAt(0) == 'R' || address.charAt(0) == 'r') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + 0x0800);
            else if (address.charAt(0) == 'D' || address.charAt(0) == 'd') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + 0x1000);
            else if (address.charAt(0) == 'B' || address.charAt(0) == 'b') addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + 0x6000);
                // 扩展区域
            else if (address.charAt(0) == 'E' || address.charAt(0) == 'e')
            {
                if      (address.charAt(1) == 'K' || address.charAt(1) == 'k') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x1000 : 0x100 )); }
                else if (address.charAt(1) == 'V' || address.charAt(1) == 'v') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x2000 : 0x200 )); }
                else if (address.charAt(1) == 'T' || address.charAt(1) == 't') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x3000 : 0x300 )); }
                else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x3000 : 0x300 )); }
                else if (address.charAt(1) == 'L' || address.charAt(1) == 'l') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x3800 : 0x380 )); }
                else if (address.charAt(1) == 'X' || address.charAt(1) == 'x') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x5800 : 0x580 )); }
                else if (address.charAt(1) == 'Y' || address.charAt(1) == 'y') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x5800 : 0x580 )); }
                else if (address.charAt(1) == 'M' || address.charAt(1) == 'm') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + ( isBit ? 0x6000 : 0x600 )); }
                // 扩展字
                else if (address.charAt(1) == 'S' || address.charAt(1) == 's') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + 0x800); }
                else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + 0x1000); }
                else if (address.charAt(1) == 'B' || address.charAt(1) == 'b')
                {
                    int add = Convert.ToInt32( address.substring( 2 ), 16 );
                    if (add < 0x8000) { addressData.PRG = 0x09; addressData.setAddressStart( add); }
                    else if (add < 0x10000) { addressData.PRG = 0x0A; addressData.setAddressStart( add - 0x8000); }
                    else { addressData.PRG = 0x0B; addressData.setAddressStart( add - 0x10000); }
                }
                else throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            else if (address.charAt(0) == 'H' || address.charAt(0) == 'h') { addressData.PRG = 0x00; addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 ) + 0x1800); }
            else if (address.charAt(0) == 'U' || address.charAt(0) == 'u') { addressData.PRG = 0x08; addressData.setAddressStart( Convert.ToInt32( address.substring( 1 ), 16 )); }
            else if (address.charAt(0) == 'G' || address.charAt(0) == 'g')
            {
                if      (address.charAt(1) == 'X' || address.charAt(1) == 'x') { addressData.PRG = 0x07; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) ); }
                else if (address.charAt(1) == 'Y' || address.charAt(1) == 'y') { addressData.PRG = 0x07; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) ); }
                else if (address.charAt(1) == 'M' || address.charAt(1) == 'm') { addressData.PRG = 0x07; addressData.setAddressStart( Convert.ToInt32( address.substring( 2 ), 16 ) + 0x1000); }
                else throw new Exception( StringResources.Language.NotSupportedDataType() );
            }
            else throw new Exception( StringResources.Language.NotSupportedDataType() );
        }
        catch( Exception ex )
        {
            return new OperateResultExOne<>( ex.getMessage() );
        }

        return OperateResultExOne.CreateSuccessResult( addressData );
    }

//endregion
}
