package com.iteaj.plguin.protocol.hj212.driver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.server.protocol.ServerInitiativeProtocol;
import com.iteaj.plguin.protocol.hj212.network.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HJ212FuncModelApi extends AbstractFuncProtocolModelApi {

    public HJ212FuncModelApi(String code, String name) {
        super(code, name);
    }

    public HJ212FuncModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public HJ212FuncModelApi(String code, String name, FuncType type) {
        super(code, name, type);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        String st = arg.getString("ST");
        String pwd = arg.getString("pwd");
        String QN = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        String deviceSn = arg.getDeviceSn();
        HJ212MessageHead head = new HJ212MessageHead(deviceSn, HJ212SysCode.getByCode(st)
                , HJ212CmdCode.getByCode(getCode()), QN, pwd, "5");
        Map<String, String> params = new HashMap<>();
        HJ212MessageBody body = new HJ212MessageBody(params);
        HJ212Message message = new HJ212Message(head, body);
        if(getCode().equals(HJ212CmdCode.设置实时数据间隔.getCode()+"")) {
            String rtdInterval = arg.getString("RtdInterval");
            if(StrUtil.isEmpty(rtdInterval)) {
                throw new ProtocolInvokeException("未指定时间间隔");
            }

            params.put("RtdInterval", rtdInterval);
        }

        return new HJ212RequestProtocol(message);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof ServerInitiativeProtocol) {
            protocol.addParam("result", result);
            ((ServerInitiativeProtocol<?>) protocol).timeout(10 * 1000).request();
        }
    }
}
