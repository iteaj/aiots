package com.iteaj.plugin.protocol.plc.ab.profinet;

import com.iteaj.plugin.protocol.plc.core.address.DeviceAddressDataBase;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

public class AllenBradleySLCAddress extends DeviceAddressDataBase {

    /**
     * 获取或设置等待读取的数据的代码<br />
     * Get or set the code of the data waiting to be read
     */
    public byte DataCode = 0x00;

    /**
     * 获取或设置PLC的DB块数据信息<br />
     * Get or set PLC DB data information
     */
    public short DbBlock = 0x00;

    /**
     * 从指定的地址信息解析成真正的设备地址信息
     * @param address 地址信息
     * @param length 数据长度
     */
    public void Parse( String address, int length ) {
        OperateResultExOne<AllenBradleySLCAddress> addressData = ParseFrom(address, (short) length);
        if (addressData.IsSuccess) {
            setAddressOffset(addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            DataCode = addressData.Content.DataCode;
            DbBlock = addressData.Content.DbBlock;
        }
    }

    public String toString( ) {
        switch (DataCode) {
            case (byte) 0x8E:
                return "A" + DbBlock + ":" + getAddressStart();
            case (byte) 0x85:
                return "B" + DbBlock + ":" + getAddressStart();
            case (byte) 0x89:
                return "N" + DbBlock + ":" + getAddressStart();
            case (byte) 0x8A:
                return "F" + DbBlock + ":" + getAddressStart();
            case (byte) 0x8D:
                return "ST" + DbBlock + ":" + getAddressStart();
            case (byte) 0x84:
                return "S" + DbBlock + ":" + getAddressStart();
            case (byte) 0x87:
                return "C" + DbBlock + ":" + getAddressStart();
            case (byte) 0x83:
                return "I" + DbBlock + ":" + getAddressStart();
            case (byte) 0x82:
                return "O" + DbBlock + ":" + getAddressStart();
            case (byte) 0x88:
                return "R" + DbBlock + ":" + getAddressStart();
            case (byte) 0x86:
                return "T" + DbBlock + ":" + getAddressStart();
            case (byte) 0x91:
                return "L" + DbBlock + ":" + getAddressStart();
        }
        return String.valueOf(getAddressStart());
    }


    /**
     * 从实际的罗克韦尔的地址里面解析出地址对象，例如 A9:0<br />
     * Parse the address object from the actual Rockwell address, such as A9:0
     * @param address 实际的地址数据信息，例如 A9:0
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<AllenBradleySLCAddress> ParseFrom( String address )
    {
        return ParseFrom( address, (short) 0 );
    }

    /**
     * 从实际的罗克韦尔的地址里面解析出地址对象，例如 A9:0<br />
     * Parse the address object from the actual Rockwell address, such as A9:0
     * @param address 实际的地址数据信息，例如 A9:0
     * @param length 读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<AllenBradleySLCAddress> ParseFrom( String address, short length )
    {
        if (!address.contains( ":" )) return new OperateResultExOne<AllenBradleySLCAddress>( "Address can't find ':', example : A9:0" );
        String[] adds = address.split( ":" );

        try
        {
            AllenBradleySLCAddress allenBradleySLC = new AllenBradleySLCAddress( );
            //OperateResult<byte, ushort, ushort> result = new OperateResult<byte, ushort, ushort>( );
            // 还有一个没有添加，   0x8f BCD
            switch (adds[0].charAt(0))
            {
                case 'A': allenBradleySLC.DataCode = (byte)0x8E; break;   // ASCII
                case 'B': allenBradleySLC.DataCode = (byte)0x85; break;   // bit
                case 'N': allenBradleySLC.DataCode = (byte)0x89; break;   // integer
                case 'F': allenBradleySLC.DataCode = (byte)0x8A; break;   // floating point
                case 'S':
                {
                    if (adds[0].length() > 1 && adds[0].charAt(1) == 'T')
                        allenBradleySLC.DataCode = (byte) 0x8D;        // string
                    else
                        allenBradleySLC.DataCode = (byte) 0x84;        // status
                    break;
                }
                case 'C': allenBradleySLC.DataCode = (byte) 0x87; break;   // counter
                case 'I': allenBradleySLC.DataCode = (byte) 0x83; break;   // input
                case 'O': allenBradleySLC.DataCode = (byte) 0x82; break;   // output
                case 'R': allenBradleySLC.DataCode = (byte) 0x88; break;   // control
                case 'T': allenBradleySLC.DataCode = (byte) 0x86; break;   // timer
                case 'L': allenBradleySLC.DataCode = (byte) 0x91; break;   // long integer
                default: throw new Exception( "Address code wrong, must be A,B,N,F,S,C,I,O,R,T,ST,L" );
            };
            switch (allenBradleySLC.DataCode)
            {
                case (byte)0x84: allenBradleySLC.DbBlock = adds[0].length() == 1 ? (short)2 : Short.parseShort( adds[0].substring( 1 ) ); break;
                case (byte)0x82: allenBradleySLC.DbBlock = adds[0].length() == 1 ? (short)0 : Short.parseShort( adds[0].substring( 1 ) ); break;
                case (byte)0x83: allenBradleySLC.DbBlock = adds[0].length() == 1 ? (short)1 : Short.parseShort( adds[0].substring( 1 ) ); break;
                case (byte)0x8D: allenBradleySLC.DbBlock = adds[0].length() == 2 ? (short)1 : Short.parseShort( adds[0].substring( 2 ) ); break;
                default:   allenBradleySLC.DbBlock = Short.parseShort( adds[0].substring( 1 ) ); break;
            }

            allenBradleySLC.setAddressStart( Short.parseShort( adds[1] ));
            return OperateResultExOne.CreateSuccessResult( allenBradleySLC );
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<AllenBradleySLCAddress>( "Wrong Address format: " + ex.getMessage() );
        }
    }
}
