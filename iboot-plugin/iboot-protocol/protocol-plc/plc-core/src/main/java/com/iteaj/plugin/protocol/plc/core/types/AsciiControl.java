package com.iteaj.plugin.protocol.plc.core.types;

/**
 * 控制型的ascii资源信息<br />
 * Controlled ascii resource information
 */
public class AsciiControl {

    /**
     * 空字符
     */
    public static final byte NUL = 0x00;

    /**
     * 标题开始<br />
     * start of headling
     */
    public static final byte SOH = 0x01;

    /**
     * 正文开始<br />
     * start of text
     */
    public static final byte STX = 0x02;

    /**
     * 正文结束<br />
     * end of text
     */
    public static final byte ETX = 0x03;

    /**
     * 传输结束<br />
     * end of transmission
     */
    public static final byte EOT = 0x04;

    /**
     * 请求<br />
     * enquiry
     */
    public static final byte ENQ = 0x05;

    /**
     * 接到通知<br />
     * acknowledge
     */
    public static final byte ACK = 0x06;

    /**
     * 响铃<br />
     * bell
     */
    public static final byte BEL = 0x07;

    /**
     * 退格<br />
     * backspace
     */
    public static final byte BS = 0x08;

    /**
     * 水平制表符<br />
     * horizontal tab
     */
    public static final byte HT = 0x09;

    /**
     * 换行符<br />
     * NL line feed, new line
     */
    public static final byte LF = 0x0a;

    /**
     * 垂直制表符<br />
     * vertical tab
     */
    public static final byte VT = 0x0b;

    /**
     * 换页键<br />
     * NP form feed, new page
     */
    public static final byte FF = 0x0c;

    /**
     * 回车键<br />
     * carriage return
     */
    public static final byte CR = 0x0d;

    /**
     * 不用切换<br />
     * shift out
     */
    public static final byte SO = 0x0e;

    /**
     * 启用切换<br />
     * shift in
     */
    public static final byte SI = 0x0f;

    /**
     * 数据链路定义<br />
     * data link escape
     */
    public static final byte DLE = 0x10;

    /**
     * 设备控制1<br />
     * device control 1
     */
    public static final byte DC1 = 0x11;

    /**
     * 设备控制2<br />
     * device control 2
     */
    public static final byte DC2 = 0x12;

    /**
     * 设备控制3<br />
     * device control 3
     */
    public static final byte DC3 = 0x13;

    /**
     * 设备控制4<br />
     * device control 4
     */
    public static final byte DC4 = 0x14;

    /**
     * 拒绝接收<br />
     * negative acknowledge
     */
    public static final byte NAK = 0x15;

    /**
     * 同步空闲<br />
     * synchronous idle
     */
    public static final byte SYN = 0x16;

    /**
     * 传输块结束<br />
     * end of trans. block
     */
    public static final byte ETB = 0x17;

    /**
     *  取消<br />
     *  cancel
     */
    public static final byte CAN = 0x18;

    /**
     * 介质中断<br />
     * end of medium
     */
    public static final byte EM = 0x19;

    /**
     * 替补<br/>
     * substitute
     */
    public static final byte SUB = 0x1a;

    /**
     * 溢出<br />
     * escape
     */
    public static final byte ESC = 0x1b;

    /**
     * 文件分隔符<br />
     * file separator
     */
    public static final byte FS = 0x1c;

    /**
     * 分组符<br />
     * group separator
     */
    public static final byte GS = 0x1d;

    /**
     * 记录分离符<br />
     * record separator
     */
    public static final byte RS = 0x1e;

    /**
     * 单元分隔符<br />
     * unit separator
     */
    public static final byte US = 0x1f;
}
