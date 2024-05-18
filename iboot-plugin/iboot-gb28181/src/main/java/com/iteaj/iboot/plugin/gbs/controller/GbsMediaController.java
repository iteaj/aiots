package com.iteaj.iboot.plugin.gbs.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.gbs.MediaInfo;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流媒体管理
 */
@RestController
@RequestMapping("/gbs/media")
public class GbsMediaController extends BaseController {

    @Autowired
    private GbsDeviceService deviceService;

    /**
     * 获取流媒体配置
     * @return
     */
    @GetMapping("config")
    public Result<MediaInfo> config() {
        MediaInfo mediaInfo = deviceService.getMediaInfo();
        return success(mediaInfo);
    }
}
