package com.iteaj.iboot.plugin.gbs.rtvs;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RtvsDeviceServiceImpl implements GbsDeviceService, InitializingBean {

    private String authorization;
    @Autowired
    private RtvsProperties properties;
    @Autowired
    private RtvsDeviceMapper deviceMapper;
    @Autowired
    private RtvsChannelMapper channelMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public GbsDevice getByDeviceId(String deviceId) {
        RtvsDevice rtvsDevice = deviceMapper.selectOne(Wrappers.<RtvsDevice>lambdaQuery().eq(RtvsDevice::getDeviceId, deviceId));
        if(rtvsDevice != null) {
            GbsDevice gbsDevice = new GbsDevice();
            BeanUtil.copyProperties(rtvsDevice, gbsDevice);
            return gbsDevice;
        }

        return null;
    }

    @Override
    public boolean deleteByDeviceId(String deviceId) {
        return deviceMapper.delete(Wrappers.<RtvsDevice>lambdaQuery().eq(RtvsDevice::getDeviceId, deviceId)) > 0;
    }

    @Override
    public Page<GbsDevice> page(Page page, GbsDevice entity) {
        RtvsDevice gbsDevice = new RtvsDevice();
        BeanUtil.copyProperties(entity, gbsDevice);
        Page<RtvsDevice> selectPage = deviceMapper.detailByPage(page, gbsDevice);
        if(!CollectionUtils.isEmpty(page.getRecords())) {
            List<GbsDevice> devices = selectPage.getRecords()
                    .stream()
                    .map(RtvsDevice::convert)
                    .collect(Collectors.toList());
            page.setRecords(devices);
        }

        return page;
    }

    @Override
    public void sync(String deviceId) {
        RtvsProperties.GbSip sip = properties.getSip();
        String url = String.format("%s%s?deviceId=%s", sip.getApiUri(), "/api/DeviceInfo/SendRefreshChannel", deviceId);
        HttpResponse response = HttpUtil.createPost(url)
                .header("authorization", this.authorization).execute();
        if(response.isOk()) {
            JSONObject jsonObject = JSONObject.parseObject(response.body());
            if(jsonObject.getInteger("code") != 200) {
                throw new ServiceException(jsonObject.getString("message"));
            } else if(!jsonObject.getBoolean("data")) {
                throw new ServiceException("同步失败");
            }
        } else {
            throw new ServiceException("同步异常");
        }
    }

    @Override
    public boolean update(GbsDevice device) {
        return deviceMapper.update(device.toRtvsDevice(), Wrappers.<RtvsDevice>lambdaUpdate()
                .eq(RtvsDevice::getDeviceId, device.getDeviceId())) > 0;
    }

    @Override
    public Page<GbsChannel> channels(Page page, GbsChannel entity) {
        Page<RtvsChannel> selectPage = channelMapper.selectPage(page, Wrappers.query(entity.toRtvsChannel()));
        if(!CollectionUtils.isEmpty(selectPage.getRecords())) {
            List<GbsChannel> channels = selectPage.getRecords().stream()
                    .map(item -> item.convert()).collect(Collectors.toList());
            page.setRecords(channels);
        }

        return page;
    }

    @Override
    public void ptzCtrl(String deviceId, String channelId, String command, int horizonSpeed, int verticalSpeed, int zoomSpeed) {
        RtvsProperties.GbSip sip = properties.getSip();
        String url = String.format("%s%s?deviceId=%s&channel=%s&", sip.getApiUri(), "/api/DeviceControl/PTZCtrl", deviceId);
        HttpResponse response = HttpUtil.createPost(url)
                .header("authorization", this.authorization).execute();
        if(response.isOk()) {
            JSONObject jsonObject = JSONObject.parseObject(response.body());
            if(jsonObject.getInteger("code") != 200) {
                throw new ServiceException(jsonObject.getString("message"));
            } else if(!jsonObject.getBoolean("data")) {
                throw new ServiceException("指令下发失败");
            }
        } else {
            throw new ServiceException("指令下发异常");
        }
    }

    /**
     * 每10分钟获取一次记录, 保证token不失效
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void authorization() {
        RtvsProperties.GbSip sip = properties.getSip();
        if(authorization == null) {
            String url = String.format("%s%s?UserName=%s&Password=%s", sip.getApiUri(), "/api/Admin/Login", sip.getUserName(), sip.getPassword());
            HttpResponse response = HttpUtil.createPost(url).execute();
            if(response.isOk()) {
                JSONObject jsonObject = JSONObject.parseObject(response.body());
                if(jsonObject.getInteger("code") == 200) {
                    this.authorization = jsonObject.getJSONObject("data").getString("Token");
                } else {
                    logger.error("<<< 登录RTVS系统失败 {}", jsonObject.getString("message"));
                }
            }
        } else {

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.authorization();
    }
}
