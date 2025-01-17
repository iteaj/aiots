package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.core.types.Encoding;

/**
 * 欧姆的Cpu的单元信息数据类
 */
public class OmronCpuUnitData {
    /**
     * 实例化一个默认的对象
     */
    public OmronCpuUnitData() { }

    /**
     * 根据原始的数据来实例化相关的CPU单元信息
     * @param data 原始字节数
     */
    public OmronCpuUnitData( byte[] data ) {
        Model   = Encoding.ASCII.GetString( data, 0, 20 ).trim( );
        Version = Encoding.ASCII.GetString( data, 20, 10 ).trim( );
        LargestEMNumber = data[41] & 0xff;
        ProgramAreaSize = (data[80] & 0xff) * 256 + data[81];
        IOMSize = (data[82] & 0xff) * 1024;
        DMSize = (data[83] & 0xff) * 256 + data[84];
        TCSize = (data[85] & 0xff) * 1024;
        EMSize = data[86] & 0xff;
    }

    /**
     * Cpu unit Model
     */
    public String Model = "";

    /**
     * CPU Unit internal system version
     */
    public String Version = "";

    /**
     * Largest number, 0 to 19, in CPU Unit’s EM area.
     */
    public int LargestEMNumber = 0;

    /**
     * Maximum size of usable program area，unit: k words
     */
    public int ProgramAreaSize = 0;

    /**
     * The size of the area (CIO, WR, HR, AR, timer/counter completion flags, TN) in which bit commands can be used( always 23). unit: bytes
     */
    public int IOMSize = 0;

    /**
     * Total words in the DM area (always 32,768)
     */
    public int DMSize = 0;

    /**
     * Among the banks in the EM area, the number of banks(0 to D ) without file memory
     *
     * @apiNote Banks (1 bank = 32,768 words)
     */
    public int EMSize = 0;

    /**
     * Maximum number of timers/counters available (always 8)
     */
    public int TCSize = 0;


    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Model: " + Model + "\r\n");
        stringBuilder.append("Version: " + Version + "\r\n");
        stringBuilder.append("LargestEMNumber: " + LargestEMNumber + "\r\n");
        stringBuilder.append("ProgramAreaSize: " + ProgramAreaSize + "\r\n");
        stringBuilder.append("IOMSize: " + IOMSize + "\r\n");
        stringBuilder.append("DMSize: " + DMSize + "\r\n");
        stringBuilder.append("TCSize: " + TCSize + "\r\n");
        stringBuilder.append("EMSize: " + EMSize);
        return stringBuilder.toString();
    }
}
