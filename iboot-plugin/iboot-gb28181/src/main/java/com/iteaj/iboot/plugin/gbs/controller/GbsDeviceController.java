package com.iteaj.iboot.plugin.gbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.gbs.GbsRealtimeTree;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.plugin.gbs.entity.GbsChannel;
import com.iteaj.iboot.plugin.gbs.entity.GbsDevice;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 国标设备管理
 */
@RestController
@RequestMapping("/gbs/device")
public class GbsDeviceController extends BaseController {

    private final GbsDeviceService deviceService;

    public GbsDeviceController(GbsDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 分页查询国标设备
     * @param page 当前页
     * @param device 设备参数
     * @return 分页国标列表
     */
    @GetMapping("/view")
    public Result<Page<GbsDevice>> view(Page page, GbsDevice device){
        deviceService.page(page, device);
        return success(page);
    }

    /**
     * 树形结构的设备-通道列表
     * @param deviceId 设备id 用于获取下面的通道列表
     * @return
     */
    @GetMapping("/trees")
    public Result<List<GbsRealtimeTree>> trees(String deviceId) {
        List<GbsRealtimeTree> trees = deviceService.trees(deviceId);
        return success(trees);
    }

    /**
     *  更新指定设备
     * @param device
     * @return
     */
    @PostMapping("update")
    @CheckPermission({"gbs:device:update"})
    public Result<Boolean> update(@RequestBody GbsDevice device) {
        deviceService.update(device);
        return success();
    }

    /**
     * 使用ID查询国标设备
     * @param deviceId 国标ID
     * @return 国标设备
     */
    @GetMapping("/edit")
    public Result<GbsDevice> edit(String deviceId){
        GbsDevice device = deviceService.getByDeviceId(deviceId);
        return success(device);
    }

    /**
     * 删除一台设备
     * @param deviceId
     * @return
     */
    @GetMapping("/del")
    @CheckPermission({"gbs:device:del"})
    public Result<Boolean> del(String deviceId) {
        boolean delete = deviceService.deleteByDeviceId(deviceId);
        return success(delete, "删除成功");
    }

    /**
     * 同步通道
     * @param device
     * @return
     */
    @PostMapping("sync")
    @CheckPermission({"gbs:device:sync"})
    public Result<Boolean> sync(@RequestBody GbsDevice device) {
        deviceService.sync(device.getDeviceId());
        return success("同步成功");
    }

    /**
     * 获取指定设备下的通道列表
     * @param entity 国标通道信息
     * @return
     */
    @GetMapping("channels")
    @CheckPermission({"gbs:device:channels"})
    public Result<Page<GbsChannel>> channels(Page page, GbsChannel entity) {
        page = deviceService.channels(page, entity);
        return success(page);
    }

    /**
     * 获取通道对象
     * @param deviceId 国标ID
     * @param channelId 通道ID
     * @return 国标通道
     */
    @GetMapping("/getChannel")
    public Result<GbsChannel> getChannel(String deviceId, String channelId) {
        GbsChannel channel = deviceService.getByDeviceIdAndChannelId(deviceId, channelId);
        return success(channel);
    }

    /**
     *  更新指定通道
     * @param device
     * @return
     */
    @PostMapping("updateChannel")
    @CheckPermission({"gbs:device:updateChannel"})
    public Result<Boolean> updateChannel(@RequestBody GbsChannel device) {
        deviceService.update(device);
        return success();
    }
}
