package com.iteaj.framework.spi.admin;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
public class LoggerEntity {

    private String id;
    private String url;
    private Object args[];
    private Object source;
    private String errMsg;

    // 操作毫秒数
    private long operaTime;
    private String[] perms;
    private String shortName;
    private LoggerType type;


    public LoggerEntity(String url, Object[] args, Object source
            , LoggerType loggerType) {
        this.url = url;
        this.args = args;
        this.source = source;
        this.type = loggerType;
    }

    public String getId() {
        return id;
    }

    public LoggerEntity setId(String id) {
        this.id = id;
        return this;
    }

    public Object[] getArgs() {
        return args;
    }

    public LoggerEntity setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public Object getSource() {
        return source;
    }

    public LoggerEntity setSource(Object source) {
        this.source = source;
        return this;
    }

    public LoggerType getType() {
        return type;
    }

    public LoggerEntity setType(LoggerType type) {
        this.type = type;
        return this;
    }

    public String[] getPerms() {
        return perms;
    }

    public LoggerEntity setPerms(String[] perms) {
        this.perms = perms;
        return this;
    }

    public long getOperaTime() {
        return operaTime;
    }

    public LoggerEntity setOperaTime(long operaTime) {
        this.operaTime = operaTime;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public LoggerEntity setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public LoggerEntity setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public LoggerEntity setUrl(String url) {
        this.url = url;
        return this;
    }
}
