package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.ProtocolType;

public enum HJ212CmdCode implements ProtocolType {
    设置超时时间及重发次数(1000),
    初始化预留(1001), // 1001-1010

    现场机时间(1011),
    设置现场机时间(1012),
    现场机时间校准请求(1013),
    提取或上传实时数据间隔(1061),
    设置实时数据间隔(1062),
    提取或上传分钟数据间隔(1063),
    设置分钟数据间隔(1064),
    设置现场机密码(1072),
    参数命令预留(1073), // 1073-1999

    上传污染物实时数据(2011),
    设备运行状态数据(2021),
    污染物日历史数据(2031),
    设备运行时间日历史数据(2041),
    污染物分钟数据(2051),
    污染物小时数据(2061),
    数采仪开机时间(2081),
    数据命令预留(2082), // 2082-2999

    请求应答(9011), // 用于现场机回应接收的上位机请求命令是否有效
    执行结果(9012), // 用于现场机回应接收的上位机请求命令执行结果
    通知应答(9013), // 回应通知命令
    数据应答(9014), // 数据应答命令

    // 预留码
    交互命令预留(9015),
    控制命令预留(3022), // 3022-3999

    ;

    private int code;

    HJ212CmdCode(int code) {
        this.code = code;
    }

    public static HJ212CmdCode getByCode(String cn) {
        int code = Integer.parseInt(cn);
        if(code >= 1001 && code <= 1010) {
            return 初始化预留;
        } else if(code >= 1073 && code <= 1999) {
            return 参数命令预留;
        } else if(code >= 2082 && code <= 2999) {
            return 数据命令预留;
        } else if(code >= 3022 && code <= 3999) {
            return 控制命令预留;
        } else if(code >= 9015 && code <= 9999) {
            return 交互命令预留;
        }

        for (HJ212CmdCode item : HJ212CmdCode.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }

        throw new ProtocolException("不支持的命令编码["+cn+"]");
    }

    public int getCode() {
        return code;
    }

    @Override
    public HJ212CmdCode getType() {
        return this;
    }

    @Override
    public String getDesc() {
        return this.name();
    }
}
