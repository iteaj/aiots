package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.collect.CollectException;
import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.server.dtu.impl.CommonDtuProtocol;
import com.iteaj.iot.utils.ByteUtil;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class CommonDtuCollectAction extends CollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_COMMON_DTU;
    }

    @Override
    public String getDesc() {
        return "通用DTU采集器";
    }

    @Override
    public void validate(Signal signal) {
        if(!StringUtils.hasText(signal.getMessage())) {
            throw new CollectException("未指定报文");
        }

        if(!StringUtils.hasText(signal.getEncode())) {
            throw new CollectException("未指定报文编码");
        }
    }

    @Override
    public void validate(CollectDetail deviceDto) {
        super.validate(deviceDto);
    }

    @Override
    protected void doExec(CollectTaskDto taskDto, CollectDetail detail, Signal signal, Consumer<String> call) {
        byte[] message;
        if(signal.getEncode().equals("HEX")) {
            message = ByteUtil.hexToByte(signal.getMessage());
        } else if(signal.getEncode().equals("ASCII")) {
            message = signal.getMessage().getBytes(StandardCharsets.US_ASCII);
        } else {
            message = signal.getMessage().getBytes(StandardCharsets.UTF_8);
        }

        DeviceDto device = detail.getDevice();
        CommonDtuProtocol protocol = new CommonDtuProtocol(device.getDeviceSn());
        byte[] read = protocol.read(message);

        if(protocol.getExecStatus() == ExecStatus.success) {
            call.accept(ByteUtil.bytesToHex(read));
        } else {
            throw new CollectException(protocol.getExecStatus().desc);
        }
    }
}
