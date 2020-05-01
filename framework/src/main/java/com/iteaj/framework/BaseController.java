package com.iteaj.framework;

import com.iteaj.util.module.mvc.BaseHttpController;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class BaseController extends BaseHttpController {

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView unException(Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        modelAndView.setViewName("/error/403");
        modelAndView.addObject("errMsg", e.getMessage());
        return modelAndView;
    }
}
