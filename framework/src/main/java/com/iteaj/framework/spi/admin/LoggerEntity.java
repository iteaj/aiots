package com.iteaj.framework.spi.admin;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class LoggerEntity {

    private String id;
    private String url;
    private Object args[];
    private Object source;
    private String errMsg;

    // 操作毫秒数
    private long operaTime;
    private String[] perms;
    private LoggerType type;
    private String shortName;

    public LoggerEntity(String url, String msg) {
        this.url = url;
        this.errMsg = msg;
    }

    public LoggerEntity(String url, Object[] args, Object source
            , LoggerType loggerType) {
        this.url = url;
        this.args = args;
        this.source = source;
        this.type = loggerType;
    }
}
