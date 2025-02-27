package com.iteaj.iboot.plugin.le5le.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.open.OpenApiUtil;
import com.iteaj.iboot.plugin.le5le.entity.Dataset;
import com.iteaj.iboot.plugin.le5le.entity.Dataset2d;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 乐吾乐大屏接口
 */
@Controller
@AllArgsConstructor
@RequestMapping("/iot/le5le/api")
@CrossOrigin(originPatterns = "*.le5le.com", methods = {RequestMethod.GET, RequestMethod.POST})
public class Le5LeApiController extends BaseController {

    private final Le5LeService le5LeService;

    /**
     * 获取组态大屏的组属性绑定
     * @param groupId
     * @return
     */
    @ResponseBody
    @GetMapping("attr")
    public List<Dataset> attr(Long groupId) {
        return le5LeService.listDatasetByDeviceGroupId(groupId);
    }

    /**
     * 获取2d图纸的组属性绑定
     * @param groupId
     * @return
     */
    @ResponseBody
    @GetMapping("2d")
    public List<Dataset2d> attr2d(Long groupId) {
        return le5LeService.list2dDatasetByDeviceGroupId(groupId);
    }

    /**
     * 执行设备指令
     * @param request
     * @return
     */
    @PostMapping("cmd")
    public String cmd(HttpServletRequest request) {
        OpenApiUtil.buildPassRequest(request);
        return "forward:/iot/instruct/command/invoke";
    }

    @PostMapping("data")
    public String data() {
        return "forward:/iot/common/count/data";
    }

    @PostMapping("warn")
    public String warn() {
        return "forward:/iot/common/count/warn";
    }

    @PostMapping("status")
    public String status() {
        return "forward:/iot/common/count/status";
    }
}
