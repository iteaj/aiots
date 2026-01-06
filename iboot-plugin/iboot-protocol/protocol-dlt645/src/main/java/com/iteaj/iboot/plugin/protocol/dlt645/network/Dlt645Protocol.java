package com.iteaj.iboot.plugin.protocol.dlt645.network;

import com.iteaj.iot.Message;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

import java.util.Date;

public interface Dlt645Protocol extends Protocol {

    Object getValue();

    ExecStatus getExecStatus();

    Dlt645Protocol setProtocolType(Dlt645ProtocolType type);

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
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().read(data);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.readReq);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    /**
     * 写数据
     * @param deviceSn 设备编号
     * @param address 电表号
     * @param data 数据标识
     * @param value 要写的数据
     * @param code 操作者代码
     * @param perm 权限 (98[密文+mac], 99[明文+mac])
     * @param pwd 密码
     * @return
     */
    default Dlt645Protocol writeBuild(String deviceSn, String address, String data, String value, String code, String perm, String pwd) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().write(data, value, code, perm, pwd);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.writeReq);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this.build(4);
    }

    /**
     * 电表清零
     * @param deviceSn
     * @param address
     * @param pwd 6位密码
     * @param perm 权限
     * @param code 8位操作者代码
     * @return
     */
    default Dlt645Protocol remBuild(String deviceSn, String address, String perm, String pwd, String code) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().rem(perm, code, pwd);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.rem);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this.build(4);
    }


    /**
     * 总事件清零
     * @param deviceSn
     * @param address
     * @param pwd 6位密码
     * @param perm 权限
     * @param code 8位操作者代码
     * @return
     */
    default Dlt645Protocol recTotalBuild(String deviceSn, String address, String perm, String pwd, String code) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().rec(perm, code, pwd, "FFFFFFFF");
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.rec);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this.build(4);
    }

    /**
     * 分事件清零
     * @param deviceSn
     * @param address
     * @param pwd 6位密码
     * @param perm 权限
     * @param code 8位操作者代码
     * @param item 需以FF开头 格式FFxxxxxx
     * @return
     */
    default Dlt645Protocol recItemBuild(String deviceSn, String address, String perm, String pwd, String code, String item) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().rec(perm, code, pwd, item);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.rec);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this.build(4);
    }

    /**
     * 最大需量清零
     * @param deviceSn
     * @param address
     * @param pwd 6位密码
     * @param perm 权限
     * @param code 8位操作者代码
     * @return
     */
    default Dlt645Protocol rmdBuild(String deviceSn, String address, String perm, String pwd, String code) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().rmd(perm, code, pwd);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.rmd);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this.build(4);
    }

    /**
     * 广播报时
     * @param deviceSn
     * @param time
     * @return
     */
    default Dlt645Protocol rtsBuild(String deviceSn, Date time) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().rts(time);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, "999999999999", Dlt645ProtocolType.rts);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    /**
     * 读通讯地址
     * @param deviceSn
     * @return
     */
    default Dlt645Protocol readMABuild(String deviceSn) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().readMA();
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, "AAAAAAAAAAAA", Dlt645ProtocolType.readMAReq);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    /**
     * 写通讯地址
     * @param deviceSn
     * @param address 通讯地址
     * @param newAddress 新通讯地址
     * @return
     */
    default Dlt645Protocol writeMABuild(String deviceSn, String address, String newAddress) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().writeMA(newAddress);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.writeMAReq);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    /**
     * 修改通讯速率
     * @param deviceSn
     * @param address
     * @param rate 通讯速率
     * @return
     */
    default Dlt645Protocol mcrBuild(String deviceSn, String address, String rate) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().writeMcr(rate);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.mcr);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    /**
     * 控制设备
     *
     * @param code 操作人代码
     * @param deviceSn
     * @param address
     * @param perm 操作权限
     * @param pwd 操作密码
     * @param cmd 命令
     * @param time 有效时间
     * @return
     */
    default Dlt645Protocol ctrlBuild(String deviceSn, String address, String code, String perm, String pwd, String cmd, Date time) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().writeCtrl(code, perm, pwd, cmd, time);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.ctrl);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    void buildDlt645RequestMessage(Dlt645MessageHead dlt645MessageHead, Dlt645DtuMessageBody dlt645DtuMessageBody);

    /**
     * 冻结命令
     * @param deviceSn
     * @param address
     * @param timeValue
     * @return
     */
    default Dlt645Protocol fCmdBuild(String deviceSn, String address, Date timeValue) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody().fCmd(timeValue);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.fCmd);
        buildDlt645RequestMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return this;
    }

    /**
     * 构建协议
     * @param prefix
     * @return
     */
    default Dlt645Protocol build(Integer prefix) {
        final Message requestMessage = requestMessage();
        final Dlt645MessageHead head = (Dlt645MessageHead) requestMessage.getHead();
        if(prefix != null && (prefix > 0 && prefix <= 4)) {
            head.setPrefix(prefix);
        }

        return this.setProtocolType(head.getType());
    }
}
