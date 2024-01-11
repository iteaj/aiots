package com.iteaj.iboot.plugin.gbs.wvp;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.genersoft.iot.vmp.service.IDeviceService;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class WvpDeviceServiceImpl implements GbsDeviceService {

    @Autowired
    private IDeviceService deviceService;

    @Override
    public GbsDevice getByDeviceId(String deviceId) {
        return null;
    }

    @Override
    public boolean deleteByDeviceId(String deviceId) {
        return false;
    }

    @Override
    public Page<GbsDevice> page(Page page, GbsDevice entity) {
        Device device = new Device();
        BeanUtil.copyProperties(entity, device);
        Page<Device> devicePage = deviceService.detailByPage(page, device);
        if(!CollectionUtils.isEmpty(devicePage.getRecords())) {
            List<GbsDevice> devices = devicePage.getRecords().stream().map(item -> {
                GbsDevice gbsDevice = new GbsDevice();
                BeanUtil.copyProperties(item, gbsDevice);
                return gbsDevice;
            }).collect(Collectors.toList());
            page.setRecords(devices);
        }

        return page;
    }

    @Override
    public void sync(String deviceId) {

    }

    @Override
    public boolean update(GbsDevice device) {
        return false;
    }

    @Override
    public Page<GbsChannel> channels(Page page, GbsChannel entity) {
        return null;
    }

    @Override
    public void ptzCtrl(String deviceId, String channelId, String command, int horizonSpeed, int verticalSpeed, int zoomSpeed) {

    }
}
