package com.iteaj.framework;

import com.iteaj.util.module.mvc.BaseHttpController;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class BaseController extends BaseHttpController {

    @Override
    protected String msgForSuccess() {
        return "操作成功";
    }
}
