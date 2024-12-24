package com.iteaj.plugin.protocol.plc.core.profinet.melsec;


/**
 * 三菱的数据类型
 * Data types of Mitsubishi PLC, here contains several commonly used types
 */
public class MelsecMcDataType {

    /**
     * 实例化一个三菱数据类型对象，如果您清楚类型代号，可以根据值进行扩展
     * Instantiate a Mitsubishi data type object, if you know the type code, you can expand according to the value
     * @param code 数据类型的代号
     * @param type 0或1，默认为0
     * @param asciiCode ASCII格式的类型信息
     * @param fromBase 指示地址的多少进制的，10或是16
     */
    public MelsecMcDataType( short code, byte type, String asciiCode, int fromBase )
    {
        DataCode = code;
        AsciiCode = asciiCode;
        FromBase = fromBase;
        if (type < 2) DataType = type;
    }



    private short DataCode = 0x00;                   // 类型代号
    private byte DataType = 0x00;                   // 数据类型
    private String AsciiCode = "";                  // ascii格式通信的字符
    private int FromBase = 0;                       // 类型


    /**
     * 数据的类型代号
     * @return
     */
    public short getDataCode() {
        return DataCode;
    }

    /**
     * 字访问还是位访问，0表示字，1表示位
     * @return
     */
    public byte getDataType() {
        return DataType;
    }

    /**
     * 当以ASCII格式通讯时的类型描述
     * @return
     */
    public String getAsciiCode() {
        return AsciiCode;
    }

    /**
     * 指示地址是10进制，还是16进制的
     * @return
     */
    public int getFromBase() {
        return FromBase;
    }


    /**
     * X输入寄存器
     */
    public final static MelsecMcDataType X = new MelsecMcDataType( (short) (0x9C), (byte) (0x01), "X*", 16 );
    /**
     * Y输出寄存器
     */
    public final static MelsecMcDataType Y = new MelsecMcDataType( (short) (0x9D), (byte) (0x01), "Y*", 16 );
    /**
     * M中间继电器
     */
    public final static MelsecMcDataType M = new MelsecMcDataType( (short) (0x90), (byte) (0x01), "M*", 10 );
    /**
     * SM特殊继电器
     */
    public final static MelsecMcDataType SM = new MelsecMcDataType( (short)0x91, (byte)0x01, "SM", 10 );
    /**
     * S步进继电器
     */
    public final static MelsecMcDataType S = new MelsecMcDataType( (short) (0x98), (byte) (0x01), "S*", 10 );
    /**
     * L锁存继电器
     */
    public final static MelsecMcDataType L = new MelsecMcDataType( (short) (0x92), (byte) (0x01), "L*", 10 );
    /**
     * F报警器
     */
    public final static MelsecMcDataType F = new MelsecMcDataType( (short) (0x93), (byte) (0x01), "F*", 10 );
    /**
     * V边沿继电器
     */
    public final static MelsecMcDataType V = new MelsecMcDataType( (short) (0x94), (byte) (0x01), "V*", 10 );
    /**
     * B链接继电器
     */
    public final static MelsecMcDataType B = new MelsecMcDataType( (short) (0xA0), (byte) (0x01), "B*", 16 );
    /**
     * SB特殊连接继电器
     */
    public final static MelsecMcDataType SB = new MelsecMcDataType( (short)0xA1, (byte)0x01, "SB", 16 );
    /**
     * DX直接访问输入
     */
    public final static MelsecMcDataType DX = new MelsecMcDataType( (short)0xA2, (byte)0x01, "DX", 16 );
    /**
     * DY直接访问输出
     */
    public final static MelsecMcDataType DY = new MelsecMcDataType( (short)0xA3, (byte)0x01, "DY", 16 );


    /**
     * D数据寄存器
     */
    public final static MelsecMcDataType D = new MelsecMcDataType( (short) (0xA8), (byte) (0x00), "D*", 10 );
    /**
     * SD特殊链接寄存器
     */
    public final static MelsecMcDataType SD = new MelsecMcDataType( (short)0xA9, (byte)0x00, "SD", 10 );
    /**
     * W链接寄存器
     */
    public final static MelsecMcDataType W = new MelsecMcDataType( (short) (0xB4), (byte) (0x00), "W*", 16 );
    /**
     * SW特殊链接寄存器
     */
    public final static MelsecMcDataType SW = new MelsecMcDataType( (short)0xB5, (byte)0x00, "SW", 16 );
    /**
     * R文件寄存器
     */
    public final static MelsecMcDataType R = new MelsecMcDataType( (short) (0xAF), (byte) (0x00), "R*", 10 );
     /**
      * 变址寄存器
     */
    public final static MelsecMcDataType Z = new MelsecMcDataType( (short) (0xCC), (byte) (0x00), "Z*", 10 );
    /**
     * 文件寄存器ZR区
     */
    public final static MelsecMcDataType ZR = new MelsecMcDataType( (short)0xB0, (byte)0x00, "ZR", 10 );

    /**
     * 定时器的值
     */
    public final static MelsecMcDataType TN = new MelsecMcDataType( (short) (0xC2), (byte) (0x00), "TN", 10 );
    /**
     * 定时器的触点
     */
    public final static MelsecMcDataType TS = new MelsecMcDataType( (short)0xC1, (byte)0x01, "TS", 10 );
    /**
     * 定时器的线圈
     */
    public final static MelsecMcDataType TC = new MelsecMcDataType( (short)0xC0, (byte)0x01, "TC", 10 );
    /**
     * 累计定时器的触点
     */
    public final static MelsecMcDataType SS = new MelsecMcDataType( (short)0xC7, (byte)0x01, "SS", 10 );
    /**
     * 累计定时器的线圈
     */
    public final static MelsecMcDataType SC = new MelsecMcDataType( (short)0xC6, (byte)0x01, "SC", 10 );
    /**
     * 累计定时器的当前值
     */
    public final static MelsecMcDataType SN = new MelsecMcDataType( (short)0xC8, (byte)0x00, "SN", 10 );
    /**
     * 计数器的值
     */
    public final static MelsecMcDataType CN = new MelsecMcDataType( (short) (0xC5), (byte) (0x00), "CN", 10 );
    /**
     * 计数器的触点
     */
    public final static MelsecMcDataType CS = new MelsecMcDataType( (short)0xC4, (byte)0x01, "CS", 10 );
    /**
     * 计数器的线圈
     */
    public final static MelsecMcDataType CC = new MelsecMcDataType( (short)0xC3, (byte)0x01, "CC", 10 );



    // region 三菱MC协议R系列PLC的地址


    /// <summary>
    /// X输入继电器
    /// </summary>
    public final static MelsecMcDataType R_X = new MelsecMcDataType(  (short) 0x009C , (byte) 0x01, "X***", 16 );

    /// <summary>
    /// Y输入继电器
    /// </summary>
    public final static MelsecMcDataType R_Y = new MelsecMcDataType( (short)0x009D, (byte)0x01, "Y***", 16 );

    /// <summary>
    /// M内部继电器
    /// </summary>
    public final static MelsecMcDataType R_M = new MelsecMcDataType( (short)0x0090, (byte)0x01, "M***", 10 );

    /// <summary>
    /// 特殊继电器
    /// </summary>
    public final static MelsecMcDataType R_SM = new MelsecMcDataType( (short)0x0091, (byte)0x01, "SM**", 10 );

    /// <summary>
    /// 锁存继电器
    /// </summary>
    public final static MelsecMcDataType R_L = new MelsecMcDataType( (short)0x0092, (byte)0x01, "L***", 10 );

    /// <summary>
    /// 报警器
    /// </summary>
    public final static MelsecMcDataType R_F = new MelsecMcDataType( (short)0x0093, (byte)0x01, "F***", 10 );

    /// <summary>
    /// 变址继电器
    /// </summary>
    public final static MelsecMcDataType R_V = new MelsecMcDataType( (short)0x0094, (byte)0x01, "V***", 10 );

    /// <summary>
    /// S步进继电器
    /// </summary>
    public final static MelsecMcDataType R_S = new MelsecMcDataType( (short)0x0098, (byte)0x01, "S***", 10 );

    /// <summary>
    /// 链接继电器
    /// </summary>
    public final static MelsecMcDataType R_B = new MelsecMcDataType( (short)0x00A0, (byte)0x01, "B***", 16 );

    /// <summary>
    /// 特殊链接继电器
    /// </summary>
    public final static MelsecMcDataType R_SB = new MelsecMcDataType( (short)0x00A1, (byte)0x01, "SB**", 16 );

    /// <summary>
    /// 直接访问输入继电器
    /// </summary>
    public final static MelsecMcDataType R_DX = new MelsecMcDataType( (short)0x00A2, (byte)0x01, "DX**", 16 );

    /// <summary>
    /// 直接访问输出继电器
    /// </summary>
    public final static MelsecMcDataType R_DY = new MelsecMcDataType( (short)0x00A3, (byte)0x01, "DY**", 16 );

    /// <summary>
    /// 数据寄存器
    /// </summary>
    public final static MelsecMcDataType R_D = new MelsecMcDataType( (short)0x00A8, (byte)0x00, "D***", 10 );

    /// <summary>
    /// 特殊数据寄存器
    /// </summary>
    public final static MelsecMcDataType R_SD = new MelsecMcDataType( (short)0x00A9, (byte)0x00, "SD**", 10 );

    /// <summary>
    /// 链接寄存器
    /// </summary>
    public final static MelsecMcDataType R_W = new MelsecMcDataType( (short)0x00B4, (byte)0x00, "W***", 16 );

    /// <summary>
    /// 特殊链接寄存器
    /// </summary>
    public final static MelsecMcDataType R_SW = new MelsecMcDataType( (short)0x00B5, (byte)0x00, "SW**", 16 );

    /// <summary>
    /// 文件寄存器
    /// </summary>
    public final static MelsecMcDataType R_R = new MelsecMcDataType( (short)0x00AF, (byte)0x00, "R***", 10 );

    /// <summary>
    /// 变址寄存器
    /// </summary>
    public final static MelsecMcDataType R_Z = new MelsecMcDataType( (short)0x00CC, (byte)0x00, "Z***", 10 );




    /// <summary>
    /// 长累计定时器触点
    /// </summary>
    public final static MelsecMcDataType R_LSTS = new MelsecMcDataType( (short)0x0059, (byte)0x01, "LSTS", 10 );
    /// <summary>
    /// 长累计定时器线圈
    /// </summary>
    public final static MelsecMcDataType R_LSTC = new MelsecMcDataType( (short)0x0058, (byte)0x01, "LSTC", 10 );
    /// <summary>
    /// 长累计定时器当前值
    /// </summary>
    public final static MelsecMcDataType R_LSTN = new MelsecMcDataType( (short)0x005A, (byte)0x00, "LSTN", 10 );

    /// <summary>
    /// 累计定时器触点
    /// </summary>
    public final static MelsecMcDataType R_STS = new MelsecMcDataType( (short)0x00C7, (byte)0x01, "STS*", 10 );
    /// <summary>
    /// 累计定时器线圈
    /// </summary>
    public final static MelsecMcDataType R_STC = new MelsecMcDataType( (short)0x00C6, (byte)0x01, "STC*", 10 );
    /// <summary>
    /// 累计定时器当前值
    /// </summary>
    public final static MelsecMcDataType R_STN = new MelsecMcDataType( (short)0x00C8, (byte)0x00, "STN*", 10 );

    /// <summary>
    /// 长定时器触点
    /// </summary>
    public final static MelsecMcDataType R_LTS = new MelsecMcDataType( (short)0x0051, (byte)0x01, "LTS*", 10 );
    /// <summary>
    /// 长定时器线圈
    /// </summary>
    public final static MelsecMcDataType R_LTC = new MelsecMcDataType( (short)0x0050, (byte)0x01, "LTC*", 10 );
    /// <summary>
    /// 长定时器当前值
    /// </summary>
    public final static MelsecMcDataType R_LTN = new MelsecMcDataType( (short)0x0052, (byte)0x00, "LTN*", 10 );

    /// <summary>
    /// 定时器触点
    /// </summary>
    public final static MelsecMcDataType R_TS = new MelsecMcDataType( (short)0x00C1, (byte)0x01, "TS**", 10 );
    /// <summary>
    /// 定时器线圈
    /// </summary>
    public final static MelsecMcDataType R_TC = new MelsecMcDataType( (short)0x00C0, (byte)0x01, "TC**", 10 );
    /// <summary>
    /// 定时器当前值
    /// </summary>
    public final static MelsecMcDataType R_TN = new MelsecMcDataType( (short)0x00C2, (byte)0x00, "TN**", 10 );

    /// <summary>
    /// 长计数器触点
    /// </summary>
    public final static MelsecMcDataType R_LCS = new MelsecMcDataType( (short)0x0055, (byte)0x01, "LCS*", 10 );
    /// <summary>
    /// 长计数器线圈
    /// </summary>
    public final static MelsecMcDataType R_LCC = new MelsecMcDataType( (short)0x0054, (byte)0x01, "LCC*", 10 );
    /// <summary>
    /// 长计数器当前值
    /// </summary>
    public final static MelsecMcDataType R_LCN = new MelsecMcDataType( (short)0x0056, (byte)0x00, "LCN*", 10 );

    /// <summary>
    /// 计数器触点
    /// </summary>
    public final static MelsecMcDataType R_CS = new MelsecMcDataType( (short)0x00C4, (byte)0x01, "CS**", 10 );
    /// <summary>
    /// 计数器线圈
    /// </summary>
    public final static MelsecMcDataType R_CC = new MelsecMcDataType( (short)0x00C3, (byte)0x01, "CC**", 10 );
    /// <summary>
    /// 计数器当前值
    /// </summary>
    public final static MelsecMcDataType R_CN = new MelsecMcDataType( (short)0x00C5, (byte)0x00, "CN**", 10 );


    // endregion



    /**
     * X输入继电器
     */
    public final static MelsecMcDataType Keyence_X = new MelsecMcDataType( (short)0x9C, (byte)0x01, "X*", 16 );
    /**
     * Y输出继电器
     */
    public final static MelsecMcDataType Keyence_Y = new MelsecMcDataType( (short)0x9D, (byte)0x01, "Y*", 16 );
    /**
     * 链接继电器
     */
    public final static MelsecMcDataType Keyence_B = new MelsecMcDataType( (short)0xA0, (byte)0x01, "B*", 16 );
    /**
     * 内部辅助继电器
     */
    public final static MelsecMcDataType Keyence_M = new MelsecMcDataType( (short)0x90, (byte)0x01, "M*", 10 );
    /**
     * 内部辅助继电器
     */
    public final static MelsecMcDataType Keyence_L = new MelsecMcDataType( (short)0x92, (byte)0x01, "L*", 10 );
    /**
     * 控制继电器
     */
    public final static MelsecMcDataType Keyence_SM = new MelsecMcDataType( (short)0x91, (byte)0x01, "SM", 10 );
    /**
     * 控制存储器
     */
    public final static MelsecMcDataType Keyence_SD = new MelsecMcDataType( (short)0xA9, (byte)0x00, "SD", 10 );
    /**
     * 数据存储器
     */
    public final static MelsecMcDataType Keyence_D = new MelsecMcDataType( (short)0xA8, (byte)0x00, "D*", 10 );
    /**
     * 文件寄存器
     */
    public final static MelsecMcDataType Keyence_R = new MelsecMcDataType( (short)0xAF, (byte)0x00, "R*", 10 );
    /**
     * 文件寄存器
     */
    public final static MelsecMcDataType Keyence_ZR = new MelsecMcDataType( (short)0xB0, (byte)0x00, "ZR", 16 );
    /**
     * 链路寄存器
     */
    public final static MelsecMcDataType Keyence_W = new MelsecMcDataType( (short)0xB4, (byte)0x00, "W*", 16 );
    /**
     * 计时器（当前值）
     */
    public final static MelsecMcDataType Keyence_TN = new MelsecMcDataType( (short)0xC2, (byte)0x00, "TN", 10 );
    /**
     * 计时器（接点）
     */
    public final static MelsecMcDataType Keyence_TS = new MelsecMcDataType( (short)0xC1, (byte)0x01, "TS", 10 );
    /**
     * 计时器（线圈）
     */
    public final static MelsecMcDataType Keyence_TC = new MelsecMcDataType( (short) 0xC0, (byte) 0x01, "TC", 10 );
    /**
     * 计数器（当前值）
     */
    public final static MelsecMcDataType Keyence_CN = new MelsecMcDataType( (short)0xC5, (byte)0x00, "CN", 10 );
    /**
     * 计数器（接点）
     */
    public final static MelsecMcDataType Keyence_CS = new MelsecMcDataType( (short)0xC4, (byte)0x01, "CS", 10 );
    /**
     * 计数器（线圈）
     */
    public final static MelsecMcDataType Keyence_CC = new MelsecMcDataType( (short) 0xC3, (byte) 0x01, "CC", 10 );


    /**
     * 输入继电器
     */
    public final static MelsecMcDataType Panasonic_X = new MelsecMcDataType( (short)0x9C, (byte)0x01, "X*", 10 );
    /**
     * 输出继电器
     */
    public final static MelsecMcDataType Panasonic_Y = new MelsecMcDataType( (short)0x9D, (byte)0x01, "Y*", 10 );
    /**
     * 链接继电器
     */
    public final static MelsecMcDataType Panasonic_L = new MelsecMcDataType( (short)0xA0, (byte)0x01, "L*", 10 );
    /**
     * 内部继电器
     */
    public final static MelsecMcDataType Panasonic_R = new MelsecMcDataType( (short)0x90, (byte)0x01, "R*", 10 );
    /**
     * 数据存储器
     */
    public final static MelsecMcDataType Panasonic_DT = new MelsecMcDataType( (short)0xA8, (byte)0x00, "D*", 10 );
    /**
     * 链接存储器
     */
    public final static MelsecMcDataType Panasonic_LD = new MelsecMcDataType( (short)0xB4, (byte)0x00, "W*", 10 );
    /**
     * 计时器（当前值）
     */
    public final static MelsecMcDataType Panasonic_TN = new MelsecMcDataType( (short)0xC2, (byte)0x00, "TN", 10 );
    /**
     * 计时器（接点）
     */
    public final static MelsecMcDataType Panasonic_TS = new MelsecMcDataType( (short)0xC1, (byte)0x01, "TS", 10 );
    /**
     * 计数器（当前值）
     */
    public final static MelsecMcDataType Panasonic_CN = new MelsecMcDataType( (short)0xC5, (byte)0x00, "CN", 10 );
    /**
     * 计数器（接点）
     */
    public final static MelsecMcDataType Panasonic_CS = new MelsecMcDataType( (short)0xC4, (byte)0x01, "CS", 10 );
    /**
     * 特殊链接继电器
     */
    public final static MelsecMcDataType Panasonic_SM = new MelsecMcDataType( (short)0x91, (byte)0x01, "SM", 10 );
    /**
     * 特殊链接存储器
     */
    public final static MelsecMcDataType Panasonic_SD = new MelsecMcDataType( (short)0xA9, (byte)0x00, "SD", 10 );
}
