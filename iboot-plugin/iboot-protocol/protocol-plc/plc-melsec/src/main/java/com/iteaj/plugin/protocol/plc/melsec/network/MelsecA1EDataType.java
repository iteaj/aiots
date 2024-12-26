package com.iteaj.plugin.protocol.plc.melsec.network;

/**
 * 三菱PLC的数据类型，此处包含了几个常用的类型
 */
public class MelsecA1EDataType {
    /**
     * 如果您清楚类型代号，可以根据值进行扩展
     * @param code 数据类型的代号
     * @param type 0或1，默认为0
     * @param asciiCode ASCII格式的类型信息
     * @param fromBase 指示地址的多少进制的，10或是16
     */
    public MelsecA1EDataType( short code, byte type, String asciiCode, int fromBase )
    {
        DataCode = code;
        AsciiCode = asciiCode;
        FromBase = fromBase;
        if (type < 2) DataType = type;
    }

    private short DataCode = 0x00;
    private byte DataType = 0x00;
    private String AsciiCode = "";
    private int FromBase = 0;

    /**
     * 类型的代号值（软元件代码，用于区分软元件类型，如：D，R）
     * @return
     */
    public short getDataCode(){
        return DataCode;
    }

    /**
     * 数据的类型，0代表按字，1代表按位
     * @return
     */
    public byte getDataType(){
        return DataType;
    }

    /**
     * 当以ASCII格式通讯时的类型描述
     * @return
     */
    public String getAsciiCode(){
        return AsciiCode;
    }

    /**
     * 指示地址是10进制，还是16进制的
     * @return
     */
    public int getFromBase(){
        return FromBase;
    }


    /**
     * X输入寄存器
     */
    public final static MelsecA1EDataType X = new MelsecA1EDataType( (short) 0x5820, (byte) 0x01, "X*", 16 );

    /**
     * Y输出寄存器
     */
    public final static MelsecA1EDataType Y = new MelsecA1EDataType( (short) 0x5920, (byte)0x01, "Y*", 16 );

    /**
     * M中间寄存器
     */
    public final static MelsecA1EDataType M = new MelsecA1EDataType( (short) 0x4D20, (byte)0x01, "M*", 10 );

    /**
     * S状态寄存器
     */
    public final static MelsecA1EDataType S = new MelsecA1EDataType( (short) 0x5320, (byte)0x01, "S*", 10 );

    /**
     * F报警器
     */
    public final static MelsecA1EDataType F = new MelsecA1EDataType( (short) 0x4620, (byte) 0x01, "F*", 10 );

    /**
     * B连接继电器
     */
    public final static MelsecA1EDataType B = new MelsecA1EDataType( (short) 0x4220, (byte)0x01, "B*", 16 );

    /**
     * TS定时器触点
     */
    public final static MelsecA1EDataType TS = new MelsecA1EDataType( (short) 0x5453, (byte) 0x01, "TS", 10 );

    /**
     * TC定时器线圈
     */
    public final static MelsecA1EDataType TC = new MelsecA1EDataType( (short) 0x5443, (byte)0x01, "TC", 10 );

    /**
     * TN定时器当前值
     */
    public final static MelsecA1EDataType TN = new MelsecA1EDataType( (short) 0x544E, (byte) 0x00, "TN", 10 );

    /**
     * CS计数器触点
     */
    public final static MelsecA1EDataType CS = new MelsecA1EDataType( (short) 0x4353, (byte) 0x01, "CS", 10 );

    /**
     * CC计数器线圈
     */
    public final static MelsecA1EDataType CC = new MelsecA1EDataType( (short) 0x4343, (byte) 0x01, "CC", 10 );

    /**
     * CN计数器当前值
     */
    public final static MelsecA1EDataType CN = new MelsecA1EDataType( (short) 0x434E, (byte) 0x00, "CN", 10 );

    /**
     * D数据寄存器
     */
    public final static MelsecA1EDataType D = new MelsecA1EDataType( (short) 0x4420, (byte)0x00, "D*", 10 );

    /**
     * W链接寄存器
     */
    public final static MelsecA1EDataType W = new MelsecA1EDataType( (short) 0x5720, (byte) 0x00, "W*", 16 );

    /**
     * R文件寄存器
     */
    public final static MelsecA1EDataType R = new MelsecA1EDataType( (short) 0x5220, (byte)0x00, "R*", 10 );
}
