package com.iteaj.plguin.protocol.hj212.network;

public enum HJ212Item {
    QN, // 毫秒时间戳 YYYYMMDDhhmmsszzz
    ST, // 系统编码
    CN, // 命令编码
    PW, // 访问密码
    MN, // 设备唯一标识
    Flag, // 拆分包应答标记
    PNUM, // 总包数
    PNO, // 包号
    CP, // 指令参数
}
