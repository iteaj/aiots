package com.iteaj.msn.core.web.controller;

import com.iteaj.framework.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * create time: 2020/6/20
 *  系统监控相关
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/core/monitor")
public class MonitorController extends BaseController {

    @GetMapping("/online")
    public String online() {
        return "online";
    }
}
