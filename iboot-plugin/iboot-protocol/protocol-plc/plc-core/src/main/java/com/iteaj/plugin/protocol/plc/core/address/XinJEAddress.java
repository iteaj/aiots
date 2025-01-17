package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;

public class XinJEAddress extends DeviceAddressDataBase {

    /**
     * 实例化一个默认的对象<br />
     * instantiate a default object
     */
    public XinJEAddress( ) {

    }

    /**
     * 指定类型，地址偏移，临界地址来实例化一个对象<br />
     * Specify the type, address offset, and critical address to instantiate an object
     * @param dataCode 数据的类型代号
     * @param address 偏移地址信息
     * @param criticalAddress 临界地址信息
     * @param station 站号信息
     */
    public XinJEAddress( byte dataCode, int address, int criticalAddress, byte station ) {
        this.DataCode = dataCode;
        this.setAddressStart(address);
        this.CriticalAddress = criticalAddress;
        this.Station = station;
    }


    /**
     * 获取或设置等待读取的数据的代码<br />
     * Get or set the code of the data waiting to be read
     */
    public byte DataCode = 0;

    /**
     * 获取或设置当前的站号信息<br />
     * Get or set the current station number information
     */
    public byte Station = 0;

    /**
     * 获取或设置协议升级时候的临界地址信息<br />
     * Get or set the critical address information when the protocol is upgraded
     */
    public int CriticalAddress = 0;

    public String toString( ) {
        return String.valueOf(getAddressStart());
    }

    /**
     * 从实际的信捷PLC的地址里面解析出地址对象<br />
     * Resolve the address object from the actual XinJE address
     * @param address 信捷的地址数据信息
     * @param length 读取的长度信息
     * @param defaultStation 默认的站号信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<XinJEAddress> ParseFrom(String address, short length, byte defaultStation ) {
        OperateResultExOne<XinJEAddress> analysis = ParseFrom(address, defaultStation);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        analysis.Content.setLength(length);
        return OperateResultExOne.CreateSuccessResult(analysis.Content);
    }


    public static OperateResultExOne<XinJEAddress> ParseFrom( String address, byte defaultStation )
    {
        try
        {
            byte stat = defaultStation;
            OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
            if (extraPara.IsSuccess) {
                stat = extraPara.Content1.byteValue();
                address = extraPara.Content2;
            }

            if      (address.startsWith( "HSCD" )) return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x8B, Integer.parseInt( address.substring( 4 ) ), Integer.MAX_VALUE  , stat ) );
            else if (address.startsWith( "ETD" ))  return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x85, Integer.parseInt( address.substring( 3 ) ), 0             , stat ) );
            else if (address.startsWith( "HSD" ))  return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x8C, Integer.parseInt( address.substring( 3 ) ), 1024          , stat));
            else if (address.startsWith( "HTD" ))  return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x89, Integer.parseInt( address.substring( 3 ) ), 1024          , stat ) );
            else if (address.startsWith( "HCD" ))  return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x8A, Integer.parseInt( address.substring( 3 ) ), 1024          , stat ) );
            else if (address.startsWith( "SFD" ))  return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x8E, Integer.parseInt( address.substring( 3 ) ), 4096          , stat ) );
            else if (address.startsWith( "HSC" ))  return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x0C, Integer.parseInt( address.substring( 3 ) ), Integer.MAX_VALUE  , stat ) );
            else if (address.startsWith( "SD" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x83, Integer.parseInt( address.substring( 2 ) ), 4096          , stat));
            else if (address.startsWith( "TD" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x81, Integer.parseInt( address.substring( 2 ) ), 4096          , stat));
            else if (address.startsWith( "CD" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x82, Integer.parseInt( address.substring( 2 ) ), 4096          , stat));
            else if (address.startsWith( "HD" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x88, Integer.parseInt( address.substring( 2 ) ), 6144          , stat));
            else if (address.startsWith( "FD" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x8D, Integer.parseInt( address.substring( 2 ) ), 8192          , stat));
            else if (address.startsWith( "ID" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x86, Integer.parseInt( address.substring( 2 ) ), 0             , stat));
            else if (address.startsWith( "QD" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x87, Integer.parseInt( address.substring( 2 ) ), 0             , stat));
            else if (address.startsWith( "SM" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x0D, Integer.parseInt( address.substring( 2 ) ), 4096          , stat));
            else if (address.startsWith( "ET" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x07, Integer.parseInt( address.substring( 2 ) ), 0             , stat));
            else if (address.startsWith( "HM" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x08, Integer.parseInt( address.substring( 2 ) ), 6144          , stat));
            else if (address.startsWith( "HS" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x09, Integer.parseInt( address.substring( 2 ) ), Integer.MAX_VALUE  , stat));
            else if (address.startsWith( "HT" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x0A, Integer.parseInt( address.substring( 2 ) ), 1024          , stat));
            else if (address.startsWith( "HC" ))   return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x0B, Integer.parseInt( address.substring( 2 ) ), 1024          , stat));
            else if (address.startsWith( "D" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x80, Integer.parseInt( address.substring( 1 ) ), 20480         , stat));
            else if (address.startsWith( "M" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x03, Integer.parseInt( address.substring( 1 ) ), 20480         , stat));
            else if (address.startsWith( "T" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x05, Integer.parseInt( address.substring( 1 ) ), 4096          , stat));
            else if (address.startsWith( "C" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x06, Integer.parseInt( address.substring( 1 ) ), 4096          , stat));
            else if (address.startsWith( "Y" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x02, Convert.ToInt32( address.substring( 1 ), 8 ), Integer.MAX_VALUE  , stat ) );
            else if (address.startsWith( "X" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x01, Convert.ToInt32( address.substring( 1 ), 8 ), Integer.MAX_VALUE  , stat ) );
            else if (address.startsWith( "S" ))    return OperateResultExOne.CreateSuccessResult( new XinJEAddress( (byte)0x04, Integer.parseInt( address.substring( 1 ) ), 8000          , stat ) );
            else return new OperateResultExOne<XinJEAddress>( StringResources.Language.NotSupportedDataType() );
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<XinJEAddress>( "ParseFrom failed: " + ex.getMessage() );
        }
    }
}
