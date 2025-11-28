package com.iteaj.iboot.plugin.gbs.rtvs;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.gbs.GbsRealtimeTree;
import com.iteaj.iboot.plugin.gbs.MediaInfo;
import com.iteaj.iboot.plugin.gbs.dto.GbsViewChannelDto;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RtvsDeviceServiceImpl implements GbsDeviceService {

    private String authorization;
    @Autowired
    private RtvsProperties properties;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public MediaInfo getMediaInfo() {
        return MediaInfo.buildRtvs(properties.getHost(), properties.getPort());
    }

    @Override
    public GbsDevice getByDeviceId(String deviceId) {
        JSON json = invokeGetRequest("/api/DeviceInfo/GetDeviceInfo?DeviceId=" + deviceId);
        return json.toJavaObject(RtvsDevice.class).convert();
    }

    @Override
    public boolean deleteByDeviceId(String deviceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Ids", Collections.singletonList(deviceId));
        JSONObject response = invokePostRequest("/api/DeviceInfo/DeleteDevice", jsonObject);
        return response.getBoolean("data");
    }

    @Override
    public List<GbsRealtimeTree> trees(String deviceId) {
        if(StrUtil.isNotBlank(deviceId)) {
            List<GbsChannel> deviceChannels = this.channels(new Page(1, -1), new GbsChannel().setDeviceId(deviceId)).getRecords();
            if(!CollectionUtils.isEmpty(deviceChannels)) {
                List<GbsRealtimeTree> realtimeTrees = deviceChannels.stream()
                        .map(item -> GbsRealtimeTree.buildChannel(item.getDeviceId(), item.getChannelId()
                                , StrUtil.isNotBlank(item.getNickName()) ? item.getNickName() : item.getName()
                                , item.getStatus()))
                        .collect(Collectors.toList());
                return realtimeTrees;
            }
        } else {
            List<GbsDevice> devices = this.page(new Page(1, -1), new GbsDevice()).getRecords();
            if(!CollectionUtils.isEmpty(devices)) {
                List<GbsRealtimeTree> realtimeTrees = devices.stream()
                        .map(item -> GbsRealtimeTree.buildDevice(item.getDeviceId()
                                , (StrUtil.isNotBlank(item.getNickName()) ? item.getNickName() : StrUtil.isBlank(item.getName()) ? item.getDeviceId() : item.getName())
                                , item.getOnline(), item.getChannelCount()))
                        .collect(Collectors.toList());

                return realtimeTrees;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Page<GbsDevice> page(Page page, GbsDevice entity) {
        RtvsDevice gbsDevice = new RtvsDevice();
        BeanUtil.copyProperties(entity, gbsDevice);
        if(entity.getName() == null) entity.setName(StrUtil.EMPTY);
        JSONObject response = invokeGetRequest(String.format("/api/DeviceInfo/GetDeviceList?deviceId=%s&DeviceName=%s&Page=%s&Limit=%s", "", entity.getName(), page.getCurrent(), page.getSize()));
        JSONArray list = response.getJSONArray("list");
        JSONObject pagination = response.getJSONObject("pagination");
        if(pagination.getInteger("total") > 0) {
            List<RtvsDevice> javaList = list.toJavaList(RtvsDevice.class);
            page.setTotal(pagination.getLong("total"));
            page.setRecords(javaList.stream().map(RtvsDevice::convert).collect(Collectors.toList()));
        }
        return page;
    }

    @Override
    public void sync(String deviceId) {
        String url = String.format("%s?deviceId=%s", "/api/DeviceInfo/SendRefreshChannel", deviceId);
        JSONObject response = invokePostRequest(url);
        if(!response.getBoolean("data")) {
            throw new ServiceException("同步失败");
        }
    }

    @Override
    public boolean update(GbsDevice device) {
        JSON body = (JSON) JSONObject.toJSON(device.toRtvsDevice());
        JSONObject jsonObject = invokePostRequest("/api/DeviceInfo/UpdateDevice", body);
        return jsonObject.getBoolean("data");
    }

    @Override
    public boolean update(GbsChannel channel) {
        RtvsChannel rtvsChannel = new RtvsChannel().setChannelId(channel.getChannelId()).setDeviceId(channel.getDeviceId())
                .setNickName(channel.getNickName());
        JSONObject jsonObject = invokePostRequest("/api/Channel/UpdateChannel", (JSON) JSONObject.toJSON(rtvsChannel));
        return jsonObject.getBoolean("data");
    }

    @Override
    public Page<GbsChannel> channels(Page page, GbsChannel entity) {
        if(entity.getDeviceId() == null) entity.setDeviceId("");
        if(entity.getChannelId() == null) entity.setChannelId("");
        JSONObject response = invokeGetRequest(String.format("/api/Channel/GetChannelList?DeviceId=%s&ChannelId=%s&Page=%s&Limit=%s"
                , entity.getDeviceId(), entity.getChannelId(), page.getCurrent(), page.getSize()));
        JSONArray list = response.getJSONArray("list");
        JSONObject pagination = response.getJSONObject("pagination");
        if(pagination.getInteger("total") > 0) {
            List<GbsChannel> collect = list.toJavaList(RtvsChannel.class).stream()
                    .map(RtvsChannel::convert).collect(Collectors.toList());
            page.setTotal(pagination.getLong("total"));
            page.setRecords(collect);
        }
        return page;
    }

    @Override
    public void ptzCtrl(String deviceId, String channelId, String command, Integer speed) {

        String param = "";
        switch (command) {
            case "up":
                param = "Up=" + speed;
                break;
            case "down":
                param = "Down=" + speed;
                break;
            case "left":
                param = "Left=" + speed;
                break;
            case "right":
                param = "Right=" + speed;
                break;
            case "upleft":
                param = "Up=" + speed + "&Left=" + speed;
                break;
            case "upright":
                param = "Up=" + speed + "&Right=" + speed;
                break;
            case "downleft":
                param = "Down=" + speed + "&Left=" + speed;
                break;
            case "downright":
                param = "Down=" + speed + "&Right=" + speed;
                break;
            case "zoomin":
                param = "ZoomIn=" + speed;
                break;
            case "zoomout":
                param = "ZoomOut=" + speed;
                break;
            case "stop":
                // 不需要参数
                break;
        }

        String url = String.format("%s?deviceId=%s&channel=%s&%s"
                , "/api/DeviceControl/PTZCtrl", deviceId, channelId, param);
        JSONObject response = invokePostRequest(url);
        if(!response.getBoolean("data")) {
            throw new ServiceException("指令下发失败");
        }
    }

    @Override
    public GbsChannel getByDeviceIdAndChannelId(String deviceId, String channelId) {
        JSON response = invokeGetRequest(String.format("/api/Channel/GetChannel?DeviceId=%s&ChannelId=%s", deviceId, channelId));
        return response.toJavaObject(RtvsChannel.class).convert();
    }

    @Override
    public List<GbsViewChannelDto> listViewChannels() {
        Page<GbsChannel> channels = this.channels(new Page(1, -1), new GbsChannel());
        if(channels.getTotal() > 0) {
            return channels.getRecords().stream().map(channel -> {
                GbsViewChannelDto channelDto = new GbsViewChannelDto();
                channelDto.setLabel(channel.getName());
                channelDto.setValue(channel.getChannelId());
                return channelDto;
            }).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public boolean deleteByDeviceAndChannelId(GbsChannel channel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DeviceId", channel.getDeviceId());
        jsonObject.put("ChannelIds", Collections.singletonList(channel.getChannelId()));
        JSONObject response = invokePostRequest("/api/Channel/DeleteChannel", jsonObject);
        return response.getBoolean("data");
    }

    private JSONObject invokeGetRequest(String url) {
        doAuthorization();
        String apiUri = properties.getSip().getApiUri();
        try (HttpResponse httpResponse = HttpUtil.createGet(apiUri + url)
                .header("authorization", this.authorization).execute()) {
            return responseHandle(httpResponse);
        }
    }

    private JSONObject responseHandle( HttpResponse httpResponse) {
        if (httpResponse.isOk()) {
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.body());
            if (jsonObject.getInteger("code") == 200) {
                Object data = jsonObject.get("data");
                if (data instanceof JSONObject) {
                    return (JSONObject) data;
                } else {
                    return jsonObject;
                }
            } else {
                throw new ServiceException("调用执行失败: " + jsonObject.getString("message"));
            }
        } else {
            String body = httpResponse.body();
            throw new ServiceException("请求rtvs失败: " + body);
        }
    }

    private JSONObject invokePostRequest(String url, JSON body) {
        doAuthorization();
        String apiUri = properties.getSip().getApiUri();
        try (HttpResponse httpResponse = HttpUtil.createPost(apiUri + url)
                .body(body.toString(), ContentType.JSON.getValue())
                .header("authorization", this.authorization).execute()) {
            return responseHandle(httpResponse);
        }
    }

    private JSONObject invokePostRequest(String url) {
        doAuthorization();
        String apiUri = properties.getSip().getApiUri();
        try (HttpResponse httpResponse = HttpUtil.createPost(apiUri + url)
                .header("authorization", this.authorization).execute()) {
            return responseHandle(httpResponse);
        }
    }

    /**
     * 每10分钟获取一次记录, 保证token不失效
     */
    @Scheduled(cron = "0 0/25 * * * ?")
    public void authorization() {
        authorization = null; // 重新登录一次
        doAuthorization();
    }

    private void doAuthorization() {
        RtvsProperties.GbSip sip = properties.getSip();
        if(authorization == null) {
            String url = String.format("%s%s?UserName=%s&Password=%s", sip.getApiUri(), "/api/Admin/Login", sip.getUserName(), sip.getPassword());
            try {
                HttpResponse response = HttpUtil.createPost(url).execute();
                if(response.isOk()) {
                    JSONObject jsonObject = JSONObject.parseObject(response.body());
                    if(jsonObject.getInteger("code") == 200) {
                        this.authorization = jsonObject.getJSONObject("data").getString("Token");
                    } else {
                        logger.error("<<< 登录RTVS系统失败 {}", jsonObject.getString("message"));
                    }
                }
            } catch (HttpException e) {
                throw new ServiceException("连接SIP服务器失败[" + sip.getApiUri() + "]");
            }
        }
    }
}
