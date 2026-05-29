package com.iteaj.framework.spi.iot.listener;

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
     * 示例: SCENARIO_AI、SCENARIO_SCREEN、SCENARIO_DEBUG、SCENARIO_SCRIPT、SCENARIO_LINKAGE、SCENARIO_APP等
     */
    private String scenario;

    public static final String SCENARIO_AI = "AI";
    public static final String SCENARIO_SCREEN = "SCREEN";
    public static final String SCENARIO_DEBUG = "DEBUG";
    public static final String SCENARIO_SCRIPT = "SCRIPT";
    public static final String SCENARIO_LINKAGE = "LINKAGE";
    public static final String SCENARIO_APP = "APP";

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
