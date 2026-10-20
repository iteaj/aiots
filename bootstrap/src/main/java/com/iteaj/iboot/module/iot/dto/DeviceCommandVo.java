package com.iteaj.iboot.module.iot.dto;

import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 设备指令VO（移动端）
 */
@Data
@Accessors(chain = true)
public class DeviceCommandVo {

    /** 接口名称 */
    private String apiName;

    /** 指令值 */
    private String commandValue;

    /** 备注 */
    private String remark;

    /** 指令类型 */
    private ProtocolApiType apiType;

    public static DeviceCommandVo of(DeviceCommandDto dto) {
        return new DeviceCommandVo()
                .setApiName(dto.getApiName())
                .setCommandValue(dto.getCommandValue())
                .setRemark(dto.getRemark())
                .setApiType(dto.getApiType());
    }
}
