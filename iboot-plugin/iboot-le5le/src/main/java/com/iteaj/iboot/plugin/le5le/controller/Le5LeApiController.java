package com.iteaj.iboot.plugin.le5le.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.open.OpenApiUtil;
import com.iteaj.iboot.plugin.le5le.entity.Dataset;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 获取组属性绑定
     * @param groupId
     * @return
     */
    @ResponseBody
    @GetMapping("attr")
    public List<Dataset> attr(Long groupId) {
        return le5LeService.listDatasetByDeviceGroupId(groupId);
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
}
