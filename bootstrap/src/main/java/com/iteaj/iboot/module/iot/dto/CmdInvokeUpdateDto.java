package com.iteaj.iboot.module.iot.dto;


import com.iteaj.framework.spi.admin.auth.AuthenticationUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CmdInvokeUpdateDto {
    /**
     * 设备uid
     */
    private String uid;

    /**
     * 设备指令
     */
    private String code;

    /**
     * 指令名称
     */
    private String name;

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 调用场景
     */
    private String scenario;

    /**
     * 控制字段
     */
    private String field;

    /**
     * 控制值
     */
    private String value;

    /**
     * 操作用户
     */
    private AuthenticationUser user;
}
