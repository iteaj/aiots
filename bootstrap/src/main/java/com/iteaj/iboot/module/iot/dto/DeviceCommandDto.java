package com.iteaj.iboot.module.iot.dto;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceCommandDto {

    private String uid;

    private String code;

    private String apiName;

    private String remark;

    private String commandName;

    private String commandValue;

    private ProtocolApiType apiType;

    public String getCommandValue() {
        if(StrUtil.isBlank(this.getUid())) {
            return String.format("api->%s%s", code, commandValue == null ? "" : "&" + commandValue);
        }

        return String.format("api->%s&uid->%s%s", code, uid, commandValue == null ? "" : "&" + commandValue);
    }

    public String getApiTypeName() {
        return apiType != null ? apiType.getDesc() : null;
    }
}
