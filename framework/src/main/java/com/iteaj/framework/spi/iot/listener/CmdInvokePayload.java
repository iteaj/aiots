package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.spi.admin.auth.AuthenticationUser;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

@Getter
@Setter
@Accessors(chain = true)
public class CmdInvokePayload implements IotEventPayload{

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
     * 操作用户
     */
    private AuthenticationUser user;

    public static final String SCENARIO_AI = "AI";
    public static final String SCENARIO_SCREEN = "大屏";
    public static final String SCENARIO_DEBUG = "调试";
    public static final String SCENARIO_SCRIPT = "脚本";
    public static final String SCENARIO_LINKAGE = "场景联动";
    public static final String SCENARIO_APP = "移动端";

    private Consumer<InvokeResult> callback = def -> {};

    public CmdInvokePayload() {
        this(null, null, 1000 * 10);
    }

    public CmdInvokePayload(String uid, String cmd) {
        this(uid, cmd, 1000 * 10);
    }

    public CmdInvokePayload(String uid, String cmd, long timeout) {
        this.uid = uid;
        this.code = cmd;
        this.timeout = timeout;
    }

    public CmdInvokePayload build(Consumer<InvokeResult> callback) {
        this.callback = callback; return this;
    }
}
