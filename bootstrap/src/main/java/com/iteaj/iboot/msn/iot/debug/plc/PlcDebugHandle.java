package com.iteaj.iboot.msn.iot.debug.plc;

import com.iteaj.framework.result.HttpResult;
import com.iteaj.iboot.msn.iot.debug.DebugHandle;
import com.iteaj.iboot.msn.iot.debug.DebugResult;
import com.iteaj.iboot.msn.iot.debug.DebugWebsocketWrite;
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
        if(model.getPlcType() == 1) {
            try {
                SiemensModel siemensModel = SiemensModel.valueOf(model.getModel());
                plcClientProtocol = new SiemensS7Protocol(new SiemensConnectProperties(model.getIp(), model.getPort(), siemensModel));
            } catch (Exception e) {
                e.printStackTrace();
                write.write(HttpResult.Fail("不支持西门子型号["+model.getModel()+"]"));
            }
        } else if(model.getPlcType() == 2){
            plcClientProtocol = new OmronTcpProtocol(new OmronConnectProperties(model.getIp(), model.getPort()));
        } else {
            write.write(HttpResult.Fail("不支持的plc类型["+model.getPlcType()+"]")); return;
        }

        Object readValue;
        DebugResult result = new DebugResult(model);
        try {
            if("read".equals(model.getCmd())) {
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
                switch (model.getType()) {
                    case "bit":
                        plcClientProtocol.write(model.getAddress(), true); break;
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
                byte[] bytes = ByteUtil.hexToByte(model.getMessage().trim());
                byte[] readFull = plcClientProtocol.readFull(bytes);
            } else {
                write.write(HttpResult.Fail("不支持的操作指令["+model.getCmd()+"]"));
            }

            ExecStatus execStatus = plcClientProtocol.getExecStatus();
            if(execStatus == ExecStatus.success) {

            }

            write.write(HttpResult.Success(result));
        } catch (Exception e) {
            write.write(HttpResult.Fail(e.getMessage()));
        }
    }
}
