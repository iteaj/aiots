package com.iteaj.msn.core.web.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * create time: 2021/3/23
 *
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/app")
public class TestAppController {

    @GetMapping("test")
    public String test() {
        return "sdf";
    }
}
