package com.iteaj.framework;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.util.CommonUtils;
import com.iteaj.framework.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * create time: 2018/7/7
 *
 * @author iteaj
 * @version 1.0
 * @since JDK1.7
 */
public abstract class AbstractController implements EnvironmentAware {

    private Environment environment;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected String msgForSuccess() {
        return "操作成功";
    }

    protected String msgForFail() {
        return "操作异常";
    }

    /**
     * 返回当前应用的运行环境
     * 默认
     * @return 如果返回{@code null}则什么都不做
     */
    protected boolean isProfile(String profile) {
        if(CommonUtils.isBlank(profile)) return false;
        else return this.environment.acceptsProfiles(Profiles.of(profile));
    }

    /**
     * @see #msgForFail() 默认使用消息
     * @return
     */
    protected <E> Result<E> fail() {
        return fail(null, msgForFail());
    }

    /**
     * @see #msgForSuccess() () 默认使用消息
     * @return
     */
    protected <E> Result<E> success() {
        return success(null, msgForSuccess());
    }

    /**
     * @see #msgForFail() 默认使用消息
     * @return
     */
    protected <E> Result<E> fail(E data) {
        return fail(data, msgForFail());
    }

    /**
     * @see #msgForSuccess() () 默认使用消息
     * @return
     */
    protected <E> Result<E> success(E data) {
        return success(data, msgForSuccess());
    }

    protected <E> Result<E> fail(String message) {
        return fail(null, message);
    }

    protected <E> Result<E> success(String message) {
        return success(null, message);
    }

    /**
     * 响应失败信息到客户端 指定消息
     * @return
     */
    protected abstract <E> Result<E> fail(E data, String msg);

    /**
     * 响应成功信息到客户端 指定消息
     * @return
     */
    protected abstract <E> Result<E> success(E data, String msg);

    /**
     * 全局异常处理
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    protected <E> Result<E> throwableHandle(Throwable e) {
        logger.error("类别：异常处理 - 动作：全局异常 - 异常信息：{}", e.getMessage(), e);

        //开发状态, 返回异常信息
        if(isProfile("prod")) {// 正式环境
            return this.fail(msgForFail());
        } else if(isProfile("dev") || isProfile("test")) { // 开发环境
            return this.fail(e.getMessage());
        } else {
            return this.fail(msgForFail()); // 未知环境
        }
    }

    /**
     * 业务异常处理
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    protected <E> Result<E> serviceHandle(ServiceException e) {
        logger.error("类别：异常处理 - 动作：业务异常 - 异常信息：{}", e.getMessage(), e);

        return this.fail(e.getMessage());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
