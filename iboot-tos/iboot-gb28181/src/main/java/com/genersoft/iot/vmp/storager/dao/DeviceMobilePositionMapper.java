package com.genersoft.iot.vmp.storager.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.genersoft.iot.vmp.gb28181.bean.MobilePosition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceMobilePositionMapper extends BaseMapper<MobilePosition> {

    default List<MobilePosition> queryPositionByDeviceIdAndTime(String deviceId, String channelId, String startTime, String endTime) {
        return this.selectList(Wrappers.<MobilePosition>lambdaQuery()
                .eq(MobilePosition::getDeviceId, deviceId)
                .eq(StrUtil.isNotBlank(channelId), MobilePosition::getChannelId, channelId)
                .ge(StrUtil.isNotBlank(startTime), MobilePosition::getTime, startTime)
                .le(StrUtil.isNotBlank(endTime), MobilePosition::getTime, endTime)
                .orderByAsc(MobilePosition::getTime));
    }

    default MobilePosition queryLatestPositionByDevice(String deviceId) {
        return this.selectOne(Wrappers.<MobilePosition>lambdaQuery()
                .eq(MobilePosition::getDeviceId, deviceId)
                .orderByDesc(MobilePosition::getTime).last("limit 1"));
    }

    default int clearMobilePositionsByDeviceId(String deviceId) {
        return this.delete(Wrappers.<MobilePosition>lambdaQuery().eq(MobilePosition::getDeviceId, deviceId));
    }

}
