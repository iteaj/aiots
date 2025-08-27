package com.iteaj.iboot.plugin.gbs.wvp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.gbs.GbsRealtimeTree;
import com.iteaj.iboot.plugin.gbs.MediaInfo;
import com.iteaj.iboot.plugin.gbs.dto.GbsViewChannelDto;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;

import java.util.List;

public class WvpDeviceServiceImpl implements GbsDeviceService {

//    @Autowired
//    private IDeviceService deviceService;

    @Override
    public MediaInfo getMediaInfo() {
        return null;
    }

    @Override
    public GbsDevice getByDeviceId(String deviceId) {
        return null;
    }

    @Override
    public boolean deleteByDeviceId(String deviceId) {
        return false;
    }

    @Override
    public List<GbsRealtimeTree> trees(String deviceId) {
        return null;
    }

    @Override
    public Page<GbsDevice> page(Page page, GbsDevice entity) {
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
    public boolean update(GbsChannel channel) {
        return false;
    }

    @Override
    public Page<GbsChannel> channels(Page page, GbsChannel entity) {
        return null;
    }

    @Override
    public void ptzCtrl(String deviceId, String channelId, String command, Integer speed) {

    }

    @Override
    public GbsChannel getByDeviceIdAndChannelId(String deviceId, String channelId) {
        return null;
    }

    @Override
    public List<GbsViewChannelDto> listViewChannels() {
        return null;
    }
}
