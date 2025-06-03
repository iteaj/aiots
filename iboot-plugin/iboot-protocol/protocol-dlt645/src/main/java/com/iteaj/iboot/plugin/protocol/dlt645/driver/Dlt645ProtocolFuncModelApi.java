package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Protocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuProtocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.serial.Dlt645SerialProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.serial.SerialComponent;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Dlt645ProtocolFuncModelApi extends AbstractFuncProtocolModelApi {

    public Dlt645ProtocolFuncModelApi(String code, String name) {
        super(code, name);
    }

    public Dlt645ProtocolFuncModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        String address = arg.getDeviceSn();
        String deviceSn = arg.getParentDeviceSn();

        final Integer prefix = arg.getDeviceConfig("prefix");
        Dlt645ProtocolType type = Dlt645ProtocolType.valueOf(getCode());
        Dlt645Protocol dlt645Protocol = this.createProtocol(type);
        if(type == Dlt645ProtocolType.rmd) {

        } else if(type == Dlt645ProtocolType.rem) {

        } else if(type == Dlt645ProtocolType.mpd) {

        } else if(type == Dlt645ProtocolType.rts) { // 广播校时
            Date timeValue;
            String time = arg.getString("time");
            if(Objects.equals(time, "{now}")) {
                timeValue = new Date();
            } else {
                timeValue = DateUtil.parse(time, "yy-MM-dd HH:mm:ss");
            }

            return dlt645Protocol.rtsBuild(deviceSn, timeValue).build(prefix);
        } else if(type == Dlt645ProtocolType.writeMAReq) { // 写电表地址
            String newAddress = arg.getString("deviceSn"); //新的电表地址
            return dlt645Protocol.writeMABuild(deviceSn, address, newAddress);
        } else if(type == Dlt645ProtocolType.fCmd || type == Dlt645ProtocolType.ffCmd) { // 冻结命令
            Date timeValue;
            String time = arg.getString("time");
            if(Objects.equals(time, "{now}")) {
                timeValue = new Date();
            } else {
                timeValue = DateUtil.parse(time, "MM-dd HH:mm");
            }

            // 使用广播地址
            if(type == Dlt645ProtocolType.ffCmd) {
                address = "999999999999";
            }

            return dlt645Protocol.fCmdBuild(deviceSn, address, timeValue).build(prefix);
        } else if(type == Dlt645ProtocolType.mcr) {
            String rate = arg.getString("rate");
            return dlt645Protocol.mcrBuild(deviceSn, address, rate).build(prefix);
        } else if(type == Dlt645ProtocolType.ctrl) {
            String perm = arg.getString("perm");
            if(StrUtil.isBlank(perm)) {
                throw new ProtocolInvokeException("认证密级未指定");
            }
            String pwd = arg.getString("pwd");
            if(StrUtil.isBlank(pwd)) {
                throw new ProtocolInvokeException("密码未指定");
            }
            String code = arg.getString("code");
            if(StrUtil.isBlank(code)) {
                throw new ProtocolInvokeException("操作者代码未指定");
            }
            String cmd = arg.getString("cmd");
            if(StrUtil.isBlank(cmd)) {
                throw new ProtocolInvokeException("控制指令未指定");
            }
            String expire = arg.getString("expire");
            if(StrUtil.isBlank(expire)) {
                throw new ProtocolInvokeException("有效时间未指定");
            }

            Date date = DateUtil.parse(expire, "yy-MM-dd HH:mm:ss");
            return dlt645Protocol.ctrlBuild(deviceSn, address, code, perm, pwd, cmd, date).build(prefix);
        }

        return null;
    }

    protected abstract Dlt645Protocol createProtocol(Dlt645ProtocolType type);

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof Dlt645DtuProtocol) {
            Dlt645DtuProtocol dtuProtocol = (Dlt645DtuProtocol) protocol;
            dtuProtocol.request();
            if(dtuProtocol.getExecStatus() == ExecStatus.success) {
                result.accept(InvokeResult.success(dtuProtocol.getValue(), dtuProtocol));
            } else {
                result.accept(InvokeResult.fail(dtuProtocol.getReason(), dtuProtocol));
            }
        } else {
            Dlt645SerialProtocol serialProtocol = (Dlt645SerialProtocol) protocol;
            IotClient iotClient = SerialComponent.instance().get(serialProtocol.getEquipCode());
            if(iotClient == null) {
                throw new ProtocolInvokeException("串口客户端不存在[com="+serialProtocol.getEquipCode()+"]");
            }

            serialProtocol.request((SerialClient) iotClient, null);
            if(serialProtocol.getExecStatus() == ExecStatus.success) {
                result.accept(InvokeResult.success(serialProtocol.getValue(), serialProtocol));
            } else {
                result.accept(InvokeResult.fail(serialProtocol.getReason(), serialProtocol));
            }
        }
    }
}
