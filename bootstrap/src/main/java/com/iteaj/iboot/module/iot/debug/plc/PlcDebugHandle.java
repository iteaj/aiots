package com.iteaj.iboot.module.iot.debug.plc;

import com.iteaj.framework.result.HttpResult;
import com.iteaj.iboot.module.iot.debug.DebugHandle;
import com.iteaj.iboot.module.iot.debug.DebugResult;
import com.iteaj.iboot.module.iot.debug.DebugWebsocketWrite;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.plc.PlcClientProtocol;
import com.iteaj.iot.plc.omron.OmronConnectProperties;
import com.iteaj.iot.plc.omron.OmronTcpProtocol;
import com.iteaj.iot.plc.siemens.SiemensConnectProperties;
import com.iteaj.iot.plc.siemens.SiemensModel;
import com.iteaj.iot.plc.siemens.SiemensS7Protocol;
import com.iteaj.iot.utils.ByteUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PlcDebugHandle implements DebugHandle<PlcDebugModel> {

    @Override
    public String type() {
        return "plc";
    }

    @Override
    public void handle(PlcDebugModel model, DebugWebsocketWrite write) {
        if(!StringUtils.hasText(model.getIp()) || model.getPort() == null) {
            write.write(HttpResult.Fail("未指定plc设备的ip地址和端口")); return;
        }

        PlcClientProtocol plcClientProtocol = null;
        if(model.getPlcType() == 2) {
            try {
                SiemensModel siemensModel = SiemensModel.valueOf(model.getModel());
                plcClientProtocol = new SiemensS7Protocol(new SiemensConnectProperties(model.getIp(), model.getPort(), siemensModel));
            } catch (Exception e) {
                e.printStackTrace();
                write.write(HttpResult.Fail("不支持西门子型号["+model.getModel()+"]")); return;
            }
        } else if(model.getPlcType() == 3){
            plcClientProtocol = new OmronTcpProtocol(new OmronConnectProperties(model.getIp(), model.getPort()));
        } else {
            write.write(HttpResult.Fail("不支持的plc类型["+model.getPlcType()+"]")); return;
        }

        Object readValue = model.getValue();
        DebugResult result = new DebugResult(model).setDeviceSn(model.getDeviceSn())
                .setReqTime(System.currentTimeMillis());
        try {
            if("read".equals(model.getCmd())) {
                if(model.getType() == null) {
                    write.write(HttpResult.Fail("错误的数据类型[null]")); return;
                }
                switch (model.getType()) {
                    case "bit":
                        readValue = plcClientProtocol.readBool(model.getAddress()); break;
                    case "short":
                        readValue = plcClientProtocol.readInt16(model.getAddress()); break;
                    case "int":
                        readValue = plcClientProtocol.readInt32(model.getAddress()); break;
                    case "long":
                        readValue = plcClientProtocol.readInt64(model.getAddress()); break;
                    case "float":
                        readValue = plcClientProtocol.readFloat(model.getAddress()); break;
                    case "double":
                        readValue = plcClientProtocol.readDouble(model.getAddress()); break;
                }
            } else if("write".equals(model.getCmd())) {
                if(model.getType() == null) {
                    write.write(HttpResult.Fail("错误的数据类型[null]")); return;
                }
                switch (model.getType()) {
                    case "bit":
                        plcClientProtocol.write(model.getAddress(), Boolean.valueOf(model.getValue())); break;
                    case "short":
                        plcClientProtocol.write(model.getAddress(), Short.valueOf(model.getValue())); break;
                    case "int":
                        plcClientProtocol.write(model.getAddress(), Integer.valueOf(model.getValue())); break;
                    case "long":
                        plcClientProtocol.write(model.getAddress(), Long.valueOf(model.getValue())); break;
                    case "float":
                        plcClientProtocol.write(model.getAddress(), Float.valueOf(model.getValue())); break;
                    case "double":
                        plcClientProtocol.write(model.getAddress(), Double.valueOf(model.getValue())); break;
                }
            } else if("message".equals(model.getCmd())) {
                if(StringUtils.hasText(model.getMessage())) {
                    byte[] bytes = ByteUtil.hexToByte(model.getMessage().trim());
                    byte[] readFull = plcClientProtocol.readFull(bytes);
                    readValue = readFull != null ? ByteUtil.bytesToHexByFormat(readFull) : "";
                } else {
                    write.write(HttpResult.Fail("请输入报文["+model.getMessage()+"]")); return;
                }
            } else {
                write.write(HttpResult.Fail("不支持的操作指令["+model.getCmd()+"]")); return;
            }

            result.setValue(readValue).setRespTime(System.currentTimeMillis())
                .setReqMsg(ByteUtil.bytesToHexByFormat(plcClientProtocol.requestMessage().getMessage()))
                .setRespMsg(ByteUtil.bytesToHexByFormat(plcClientProtocol.responseMessage().getMessage()));
            ExecStatus execStatus = plcClientProtocol.getExecStatus();
            if(execStatus != ExecStatus.success) {
                write.write(HttpResult.StatusCode(result, execStatus.desc, 208));
            } else {
                write.write(HttpResult.Success(result));
            }

        } catch (Exception e) {
            write.write(HttpResult.Fail(e.getMessage()));
        }
    }
}
