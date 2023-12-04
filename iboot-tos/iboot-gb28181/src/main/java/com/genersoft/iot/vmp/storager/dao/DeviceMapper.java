package com.genersoft.iot.vmp.storager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genersoft.iot.vmp.gb28181.bean.Device;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设备信息
 */
@Mapper
@Repository
public interface DeviceMapper extends BaseMapper<Device> {

    Device getDeviceByDeviceId(String deviceId);

    List<Device> getDevices(Boolean onLine);
}
