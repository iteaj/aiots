package com.iteaj.develop.gen.controller;

import com.iteaj.develop.entity.GenModule;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * create time: 2020/4/13
 *  代码生成api
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/dev/code")
public class CodeGenController extends BaseController {



    @PostMapping("gen")
    public Result gen(GenModule module) {
        return success();
    }
}
