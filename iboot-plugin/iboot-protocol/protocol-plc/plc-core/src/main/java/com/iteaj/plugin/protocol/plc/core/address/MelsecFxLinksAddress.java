package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

public class MelsecFxLinksAddress extends DeviceAddressDataBase {

    /**
     * 实例化一个默认的对象
     */
    public MelsecFxLinksAddress( )
    {

    }

    /**
     * 当前的地址类型信息
     */
    public String TypeCode = "";

    public void Parse( String address, int length )
    {
        super.Parse( address, length );
    }

    public String toString( )
    {
        switch( TypeCode)
        {
            case "X":
            case "Y": {
                String add = Convert.ToString( getAddressStart(), 8 );
                return TypeCode + Utilities.PadLeft( add, getAddressStart() >= 10000 ? 6 : 4, '0' );
            }
            case "M":
            case "S":
            case "TS":
            case "TN":
            case "CS":
            case "CN":
            case "D":
            case "R":
            default: {
                String add = Convert.ToString(getAddressStart(), 10);
                return TypeCode + Utilities.PadLeft(add, ((getAddressStart() >= 10000 ? 7 : 5) - TypeCode.length()));
            }
        }
    }

    public static OperateResultExOne<MelsecFxLinksAddress> ParseFrom(String address ) {
        return ParseFrom(address, 0);
    }

    /**
     * 从三菱FxLinks协议里面解析出实际的地址信息
     * @param address 三菱的地址信息
     * @param length 读取的长度信息
     * @return 解析结果信息
     */
    public static OperateResultExOne<MelsecFxLinksAddress> ParseFrom( String address, int length ) {
        MelsecFxLinksAddress melsecFxLinks = new MelsecFxLinksAddress();
        melsecFxLinks.setLength(length);
        try {
            switch (address.charAt(0)) {
                case 'X':
                case 'x': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 8));
                    melsecFxLinks.TypeCode = "X";
                    break;
                }
                case 'Y':
                case 'y': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 8));
                    melsecFxLinks.TypeCode = "Y";
                    break;
                }
                case 'M':
                case 'm': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "M";
                    break;
                }
                case 'S':
                case 's': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "S";
                    break;
                }
                case 'T':
                case 't': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "TS";
                        break;
                    } else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "TN";
                        break;
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                }
                case 'C':
                case 'c': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "CS";
                        break;
                    } else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "CN";
                        break;
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                }
                case 'D':
                case 'd': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "D";
                    break;
                }
                case 'R':
                case 'r': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "R";
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            return OperateResultExOne.CreateSuccessResult(melsecFxLinks);
        } catch (Exception ex) {
            return new OperateResultExOne<MelsecFxLinksAddress>("Address Create failed: " + ex.getMessage());
        }
    }
}
