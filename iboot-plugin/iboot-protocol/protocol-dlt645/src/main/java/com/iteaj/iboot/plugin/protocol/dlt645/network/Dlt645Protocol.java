package com.iteaj.iboot.plugin.protocol.dlt645.network;

import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

import java.util.Date;

public interface Dlt645Protocol extends Protocol {

    Object getValue();

    ExecStatus getExecStatus();

    default Dlt645Protocol readBuild(String deviceSn, String address, String data) {
        return readBuild(deviceSn, address, data, null);
    }


    default Dlt645Protocol readBuild(String deviceSn, String address, String data, Byte blocks) {
        return readBuild(deviceSn, address, data, blocks, null);
    }

    /**
     * 构建读协议
     * @param deviceSn 设备编号
     * @param address 地址域
     * @param data 数据域
     * @param blocks 要读取的块数量  {@link com.sun.istack.internal.Nullable}
     * @param date 要读取的时间  {@link com.sun.istack.internal.Nullable}
     * @return 协议
     */
    default Dlt645Protocol readBuild(String deviceSn, String address, String data, Byte blocks, Date date) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody(data).read();
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.readReq);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    void buildDlt645RequestMessage(Dlt645MessageHead dlt645MessageHead, Dlt645DtuMessageBody dlt645DtuMessageBody);
}
