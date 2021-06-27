package com.iteaj.framework.shiro;

import com.iteaj.framework.spi.admin.LoggerType;
import com.iteaj.framework.spi.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * create time: 2020/4/21
 *  基于shiro权限的事件
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthorEvent extends Event {

    private Method method;
    private Object[] args;
    private String errMsg;
    private long operaTimeMillis;
    private LoggerType type;
    private HttpServletRequest request;
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ShiroAuthorEvent(Object source, HttpServletRequest request, LoggerType type) {
        super(source);
        this.type = type;
        this.request = request;
    }

    public LoggerType getType() {
        return type;
    }

    public ShiroAuthorEvent setType(LoggerType type) {
        this.type = type;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public ShiroAuthorEvent setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Object[] getArgs() {
        return args;
    }

    public ShiroAuthorEvent setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public ShiroAuthorEvent setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    public long getOperaTimeMillis() {
        return operaTimeMillis;
    }

    public ShiroAuthorEvent setOperaTimeMillis(long operaTimeMillis) {
        this.operaTimeMillis = operaTimeMillis;
        return this;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public ShiroAuthorEvent setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }
}
