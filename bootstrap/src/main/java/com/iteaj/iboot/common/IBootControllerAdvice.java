package com.iteaj.iboot.common;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.HttpResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.auth.SecurityException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class IBootControllerAdvice {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 全局未知异常处理
     * @return
     */
    @ExceptionHandler(Throwable.class)
    protected <E> Result<E> exceptionHandle(Throwable e) {
        logger.error("未知错误", e.getMessage(), e);

        return Result.fail("系统异常");
    }

    @ExceptionHandler(ShiroException.class)
    protected Result shiroExceptionHandle(ShiroException e) {
        if(e instanceof UnauthorizedException) {
            return Result.fail("您无此功能权限");
        } else if(e instanceof AuthenticationException) {
            return Result.fail("请先认证");
        } else {
            return Result.fail("未认证或者无此权限");
        }
    }

    /**
     * 业务异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    protected <E> Result<E> serviceHandle(ServiceException e) {
        logger.error("业务执行失败", e);
        return HttpResult.Fail(e.getMessage());
    }

    /**
     * 认证授权异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(SecurityException.class)
    protected <E> Result<E> securityHandle(SecurityException e) {
        logger.error("认证授权失败", e);
        return HttpResult.Fail(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        // 只返回第一个错误信息
        return HttpResult.Fail(fieldErrors.get(0).getDefaultMessage());
    }

    // 处理 json 请求体调用接口校验失败抛出的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        // 只返回第一个错误信息
        return HttpResult.Fail(fieldErrors.get(0).getDefaultMessage());
    }

    // 处理单个参数校验失败抛出的异常
    @ExceptionHandler(ConstraintViolationException.class)
    public Result constraintViolationExceptionHandler(ConstraintViolationException e) {
        final ConstraintViolation<?> violation = e.getConstraintViolations().stream().findFirst().get();

        // 只返回第一个错误信息
        return HttpResult.Fail(violation.getMessage());
    }
}
