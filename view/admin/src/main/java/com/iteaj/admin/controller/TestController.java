package com.iteaj.admin.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.iteaj.admin.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private ITestService testService;

    @ResponseBody
    @GetMapping("/rpc")
    public String rpcRef() {
        String test = testService.getTest();
        return test;
    }

    @GetMapping("/page")
    public String page() {
        return "test";
    }
}
