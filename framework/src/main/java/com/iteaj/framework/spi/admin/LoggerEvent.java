package com.iteaj.framework.spi.admin;

import com.iteaj.framework.spi.event.Event;
import lombok.experimental.Accessors;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
@Accessors(chain = true)
public class LoggerEvent extends Event {

    private String id;
    private String url;
    private String errMsg;

    // 操作毫秒数
    private long operaTime;
    private LoggerType type;

    public LoggerEvent(String url, String errMsg) {
        super(url);
        this.errMsg = errMsg;
    }

    public String getId() {
        return id;
    }

    public LoggerEvent setId(String id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public LoggerEvent setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public Object getSource() {
        return source;
    }

    public LoggerEvent setSource(Object source) {
        this.source = source;
        return this;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public LoggerEvent setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    public long getOperaTime() {
        return operaTime;
    }

    public LoggerEvent setOperaTime(long operaTime) {
        this.operaTime = operaTime;
        return this;
    }

    public LoggerType getType() {
        return type;
    }

    public LoggerEvent setType(LoggerType type) {
        this.type = type;
        return this;
    }
}
