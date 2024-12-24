package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.core.address.DeviceAddressDataBase;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;

/**
 * 欧姆龙的Fins协议的地址类对象
 */
public class OmronFinsAddress extends DeviceAddressDataBase {

    /**
     * 获取位操作指令
     * @return 位操作指令
     */
    public byte getBitCode() {
        return BitCode;
    }

    /**
     * 设置位操作的指令
     * @param bitCode 位操作指令
     */
    public void setBitCode(byte bitCode) {
        BitCode = bitCode;
    }

    /**
     * 获取字操作的指令
     * @return 字操作指令
     */
    public byte getWordCode() {
        return WordCode;
    }

    /**
     * 设置字操作指令
     * @param wordCode 字操作指令
     */
    public void setWordCode(byte wordCode) {
        WordCode = wordCode;
    }

    private byte BitCode = 0;
    private byte WordCode = 0;


    public void Parse( String address, int length ) {
        OperateResultExOne<OmronFinsAddress> addressData = ParseFrom(address, (short) length, OmronPlcType.CSCJ);
        if (addressData.IsSuccess) {
            setAddressStart(addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            setBitCode(addressData.Content.getBitCode());
            setWordCode(addressData.Content.getWordCode());
        }
    }


    // region Static Method

    private static int CalculateBitIndex( String address ) {
        String[] splits = address.split("\\.");
        int addr = Integer.parseInt(splits[0]) * 16;
        // 包含位的情况，例如 D100.F
        if (splits.length > 1) addr += PlcHelper.CalculateBitStartIndex(splits[1]);
        return addr;
    }

    /**
     * 从实际的欧姆龙的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Profinet.Omron address
     * @param address 欧姆龙的地址数据信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<OmronFinsAddress> ParseFrom( String address ) {
        return ParseFrom(address, (short) 0, OmronPlcType.CSCJ);
    }

    /**
     * 从实际的欧姆龙的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Profinet.Omron address
     * @param address 欧姆龙的地址数据信息
     * @param length 读取的数据长度
     * @param plcType PLC的类型信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<OmronFinsAddress> ParseFrom(String address, short length, OmronPlcType plcType) {
        OmronFinsAddress addressData = new OmronFinsAddress();
        try {
            addressData.setLength(length);

            if (address.startsWith( "DR" ) || address.startsWith( "dr" ))
            {
                if (plcType == OmronPlcType.CV){
                    addressData.setWordCode((byte) 0x9C);  // CV Mode 为 9C
                    addressData.setAddressStart(CalculateBitIndex( address.substring( 2 ) ) + 0x03 * 16);
                }
                else {
                    addressData.setWordCode((byte) 0xBC);
                    addressData.setAddressStart(CalculateBitIndex( address.substring( 2 ) ) + 0x200 * 16);
                }
            }
            else if (address.startsWith( "IR" ) || address.startsWith( "ir" ))
            {
                addressData.setWordCode( (byte) 0xDC);
                addressData.setAddressStart(CalculateBitIndex( address.substring( 2 ) ) + 0x100 * 16);
            }
            else if (address.startsWith( "DM" ) || address.startsWith( "dm" ))
            {
                // DM区数据
                addressData.BitCode      = OmronFinsDataType.DM.getBitCode();
                addressData.WordCode     = OmronFinsDataType.DM.getWordCode();
                addressData.setAddressStart(CalculateBitIndex( address.substring( 2 ) ));
            }
            else if (address.startsWith( "TIM" ) || address.startsWith( "tim" ))
            {
                if (plcType == OmronPlcType.CV){
                    addressData.BitCode = (byte) 0x01;
                    addressData.WordCode = (byte) 0x81;
                }
                else {
                    addressData.BitCode      = OmronFinsDataType.TIM.getBitCode();
                    addressData.WordCode     = OmronFinsDataType.TIM.getWordCode();
                }
                addressData.setAddressStart( CalculateBitIndex( address.substring( 3 ) ));
            }
            else if (address.startsWith( "CNT" ) || address.startsWith( "cnt" ))
            {
                if (plcType == OmronPlcType.CV){
                    addressData.BitCode = (byte) 0x01;
                    addressData.WordCode = (byte) 0x81;
                    addressData.setAddressStart(CalculateBitIndex( address.substring( 3 ) ) + 0x800 * 16);
                }
                else {
                    addressData.BitCode      = OmronFinsDataType.TIM.getBitCode();
                    addressData.WordCode     = OmronFinsDataType.TIM.getWordCode();
                    addressData.setAddressStart( CalculateBitIndex( address.substring( 3 ) ) + 0x8000 * 16);
                }
            }
            else if (address.startsWith( "CIO" ) || address.startsWith( "cio" ))
            {
                if (plcType == OmronPlcType.CV) {
                    addressData.BitCode = (byte) 0x00;
                    addressData.WordCode = (byte) 0x80;
                }
                else {
                    addressData.BitCode      = OmronFinsDataType.CIO.getBitCode();
                    addressData.WordCode     = OmronFinsDataType.CIO.getWordCode();
                }
                addressData.setAddressStart( CalculateBitIndex( address.substring( 3 ) ));
            }
            else if (address.startsWith( "WR" ) || address.startsWith( "wr" ))
            {
                addressData.BitCode      = OmronFinsDataType.WR.getBitCode();
                addressData.WordCode     = OmronFinsDataType.WR.getWordCode();
                addressData.setAddressStart( CalculateBitIndex( address.substring( 2 ) ));
            }
            else if (address.startsWith( "HR" ) || address.startsWith( "hr" ))
            {
                addressData.BitCode      = OmronFinsDataType.HR.getBitCode();
                addressData.WordCode     = OmronFinsDataType.HR.getWordCode();
                addressData.setAddressStart( CalculateBitIndex( address.substring( 2 ) ));
            }
            else if (address.startsWith( "AR" ) || address.startsWith( "ar" ))
            {
                if (plcType == OmronPlcType.CV){
                    addressData.BitCode = (byte) 0x00;
                    addressData.WordCode = (byte) 0x80;
                    addressData.setAddressStart( CalculateBitIndex( address.substring( 2 ) ) + 0xB00 * 16);
                }
                else {
                    addressData.BitCode      = OmronFinsDataType.AR.getBitCode();
                    addressData.WordCode     = OmronFinsDataType.AR.getWordCode();
                    addressData.setAddressStart( CalculateBitIndex( address.substring( 2 ) ));
                }
            }
            else if (address.startsWith( "CF" ) || address.startsWith( "cf" ))
            {
                addressData.BitCode = 0x07;
                addressData.setAddressStart( CalculateBitIndex( address.substring( 2 ) ));
            }
            else if (address.startsWith( "EM" ) || address.startsWith( "em" ) || address.startsWith( "E" ) || address.startsWith( "e" ))
            {
                // E区，比较复杂，需要专门的计算
                String[] splits = address.split( "\\." );
                int block = Integer.parseInt( splits[0].substring( (address.charAt(1) == 'M' || address.charAt(1) == 'm') ? 2 : 1 ), 16 );
                if (block < 16)
                {
                    addressData.BitCode  = (byte)(0x20 + block);
                    if (plcType == OmronPlcType.CV){
                        addressData.WordCode = (byte)(0x90 + block);
                    }
                    else {
                        addressData.WordCode = (byte)(0xA0 + block);
                    }
                }
                else
                {
                    addressData.BitCode  = (byte)(0xE0 + block - 16);
                    addressData.WordCode = (byte)(0x60 + block - 16);
                }
                addressData.setAddressStart( CalculateBitIndex( address.substring( address.indexOf( '.' ) + 1 ) ));
            }
            else if (address.startsWith( "D" ) || address.startsWith( "d" ))
            {
                // DM区数据
                addressData.BitCode      = OmronFinsDataType.DM.getBitCode();
                addressData.WordCode     = OmronFinsDataType.DM.getWordCode();
                addressData.setAddressStart( CalculateBitIndex( address.substring( 1 ) ));
            }
            else if (address.startsWith( "C" ) || address.startsWith( "c" ))
            {
                if (plcType == OmronPlcType.CV){
                    addressData.BitCode = (byte) 0x00;
                    addressData.WordCode = (byte) 0x80;
                }
                else {
                    addressData.BitCode      = OmronFinsDataType.CIO.getBitCode();
                    addressData.WordCode     = OmronFinsDataType.CIO.getWordCode();
                }
                addressData.setAddressStart( CalculateBitIndex( address.substring( 1 ) ));
            }
            else if (address.startsWith( "W" ) || address.startsWith( "w" ))
            {
                addressData.BitCode      = OmronFinsDataType.WR.getBitCode();
                addressData.WordCode     = OmronFinsDataType.WR.getWordCode();
                addressData.setAddressStart( CalculateBitIndex( address.substring( 1 ) ));
            }
            else if (address.startsWith( "H" ) || address.startsWith( "h" ))
            {
                addressData.BitCode      = OmronFinsDataType.HR.getBitCode();
                addressData.WordCode     = OmronFinsDataType.HR.getWordCode();
                addressData.setAddressStart( CalculateBitIndex( address.substring( 1 ) ));
            }
            else if (address.startsWith( "A" ) || address.startsWith( "a" ))
            {
                if (plcType == OmronPlcType.CV){
                    addressData.BitCode = (byte) 0x00;
                    addressData.WordCode = (byte) 0x80;
                    addressData.setAddressStart( CalculateBitIndex( address.substring( 1 ) ) + 0xB00 * 16);
                }
                else {
                    addressData.BitCode      = OmronFinsDataType.AR.getBitCode();
                    addressData.WordCode     = OmronFinsDataType.AR.getWordCode();
                    addressData.setAddressStart( CalculateBitIndex( address.substring( 1 ) ));
                }
            }
            else
            {
                throw new Exception( StringResources.Language.NotSupportedDataType() );
            }

        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }

        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    // endregion
}
