package com.iteaj.iboot.plugin.gbs.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备云台控制
 */
@RestController
@RequestMapping("/gbs/ptz")
public class GbsPtzController extends BaseController {

    private final GbsDeviceService deviceService;

    public GbsPtzController(GbsDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     *  ptz云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param command 控制指令,允许值: left, right, up, down, upleft, upright, downleft, downright, zoomin, zoomout, stop
     * @param speed 速度(上下左右为0-255, 缩放为0-15)
     * @return
     */
    @PostMapping("/ctrl/{deviceId}/{channelId}")
    public Result<Boolean> ctrl(@PathVariable String deviceId, @PathVariable String channelId, String command, Integer speed) {

        deviceService.ptzCtrl(deviceId, channelId, command, speed);

        return success();
    }
}
