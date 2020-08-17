package com.iteaj.develop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * create time: 2020/8/16
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/dev/online")
public class OnlineController {

    @GetMapping
    public String online() {
        return "online";
    }
}
