package com.iteaj.iboot.msn.iot.dto;

import com.iteaj.iboot.msn.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.msn.iot.entity.Device;
import lombok.Data;

@Data
public class DeviceDto extends Device {

    private DeviceTypeAlias alias;
}
