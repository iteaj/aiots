package com.iteaj.framework.logger;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * create time: 2021/7/1
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class AccessLogger {

    /**
     * 访问地址
     */
    private String url;

    /**
     * 访问ip
     */
    private String ip;

    /**
     * 执行时间
     */
    private long execTime;

    /**
     * 成功或者失败说明
     */
    private String remark;

    /**
     * 执行状态 true 成功 false 失败
     */
    private boolean status;

    public AccessLogger(String url, String remark) {
        this.url = url;
        this.remark = remark;
    }
}
