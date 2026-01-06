package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import org.jetbrains.annotations.NotNull;

public interface Dlt645ProtocolSupplier {

    default ProtocolModel createProtocolModel(String protocolCode, TransportProtocol protocol) {
            return new DefaultProtocolModel(protocolCode, CtrlMode.POINT, protocol)
                .addModelAttr(new ProtocolModelAttr("value", "值", DataType.t_any, AttrType.IGNORE, "采集到的值"))
                .addModelAttr(new ProtocolModelAttr("rate", "波特率", DataType.t_string, AttrType.IGNORE, "通讯波特率"))
                .addModelAttr(new ProtocolModelAttr("data", "数据标识", DataType.t_string, AttrType.IGNORE, "长度为8的数据标识"))
                .addModelAttr(new ProtocolModelAttr("time", "时间值", DataType.t_string, AttrType.IGNORE, "时间格式yy-MM-dd HH:mm:ss"))
                .addModelAttr(new ProtocolModelAttr("expire", "有效时间", DataType.t_string, AttrType.IGNORE, "时间格式yy-MM-dd HH:mm:ss"))
                .addModelAttr(new ProtocolModelAttr("deviceSn", "电表编号", DataType.t_string, AttrType.IGNORE, "12位电表编号"))
                .addModelAttr(new ProtocolModelAttr("cmd", "控制命令", DataType.t_string, AttrType.IGNORE, "控制命令"))
                .addModelAttr(new ProtocolModelAttr("pwd", "密码", DataType.t_string, AttrType.IGNORE, "6位密码/密钥"))
                .addModelAttr(new ProtocolModelAttr("code", "操作者代码", DataType.t_string, AttrType.IGNORE, "8位操作者代码"))
                .addModelAttr(new ProtocolModelAttr("perm", "认证密级", DataType.t_string, AttrType.IGNORE, "明文或者密文传输"))
                .addModelApi(createEventModelApi(Dlt645ProtocolType.readReq.name(), Dlt645ProtocolType.readReq.getDesc(), "读取电表数据")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("data", "数据标识"))
                        .addUpConfig(ProtocolModelApiConfig.upBuild("value", "值")))
                    .addModelApi(createEventModelApi(Dlt645ProtocolType.readMAReq.name(), Dlt645ProtocolType.readMAReq.getDesc(), "读电表编号")
                            .addUpConfig(ProtocolModelApiConfig.upBuild("deviceSn", "电表编号")))
                .addModelApi(createFuncModelApi(Dlt645ProtocolType.rts.name(), Dlt645ProtocolType.rts.getDesc(), "广播校时")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("time", "格式yy-MM-dd HH:mm:ss, 支持占位符{now}")))
                .addModelApi(createFuncModelApi(Dlt645ProtocolType.mcr.name(), Dlt645ProtocolType.mcr.getDesc(), "修改波特率")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("rate", "通讯波特率")
                                .addOption("19200", "73")
                                .addOption("9600", "53")
                                .addOption("4800", "43")
                                .addOption("2400", "3B")
                                .addOption("1200", "37")
                                .addOption("600", "35")))
                .addModelApi(createFuncModelApi(Dlt645ProtocolType.fCmd.name(), Dlt645ProtocolType.fCmd.getDesc(), "冻结命令")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("time", "格式MM-dd HH:mm, 支持占位符{now}")))
                    .addModelApi(createFuncModelApi(Dlt645ProtocolType.ffCmd.name(), Dlt645ProtocolType.ffCmd.getDesc(), "广播冻结命令")
                            .addDownConfig(ProtocolModelApiConfig.downBuild("time", "格式MM-dd HH:mm, 支持占位符{now}")))
                .addModelApi(createFuncModelApi(Dlt645ProtocolType.writeMAReq.name(), Dlt645ProtocolType.writeMAReq.getDesc(), "修改电表地址")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("deviceSn", "新电表地址")))
                .addModelApi(createFuncModelApi(Dlt645ProtocolType.ctrl.name(), Dlt645ProtocolType.ctrl.getDesc(), "控制设备")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("perm", "认证密级")
                                .addOption("明文(02)", "02").addOption("密文(98)", "98"))
                        .addDownConfig(ProtocolModelApiConfig.downBuild("cmd", "命令")
                                .addOption("跳闸", "1A").addOption("合闸允许", "1B")
                                .addOption("报警", "2A").addOption("报警解除", "2B")
                                .addOption("保电", "3A").addOption("保电解除", "3B"))
                        .addDownConfig(ProtocolModelApiConfig.downBuild("pwd", "6位密码"))
                        .addDownConfig(ProtocolModelApiConfig.downBuild("code", "8位操作者代码"))
                        .addDownConfig(ProtocolModelApiConfig.downBuild("expire", "格式yy-MM-dd HH:mm:ss"))
                )
//                .addModelApi(createModelApi(Dlt645ProtocolType.mpd.name(), Dlt645ProtocolType.mpd.getDesc(), "设置电表密码"))
//                .addModelApi(createModelApi(Dlt645ProtocolType.rem.name(), Dlt645ProtocolType.rem.getDesc(), Dlt645ProtocolType.rem.getDesc()))
//                .addModelApi(createModelApi(Dlt645ProtocolType.rmd.name(), Dlt645ProtocolType.rmd.getDesc(), Dlt645ProtocolType.rmd.getDesc()))
                    ;
    }

    Dlt645ProtocolFuncModelApi createFuncModelApi(String name, String desc, String remark);

    @NotNull
    Dlt645ProtocolEventModelApi createEventModelApi(String name, String desc, String remark);
}
