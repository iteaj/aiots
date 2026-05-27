package com.iteaj.iboot.module.iot.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.mapper.DeviceCmdLogMapper;
import com.iteaj.iboot.module.iot.service.IDeviceCmdLogService;
import org.springframework.stereotype.Service;

/**
 * 设备指令下发日志 Service 实现
 */
@Service
public class DeviceCmdLogServiceImpl extends BaseServiceImpl<DeviceCmdLogMapper, DeviceCmdLog> implements IDeviceCmdLogService {

}
