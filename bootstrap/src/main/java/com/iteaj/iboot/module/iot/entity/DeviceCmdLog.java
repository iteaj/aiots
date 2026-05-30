package com.iteaj.iboot.module.iot.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEventPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 设备指令下发日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_device_cmd_log")
public class DeviceCmdLog extends BaseEntity implements IotEventPayload {

    // ========== 执行状态常量 ==========
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";

    // ========== 下发方式常量 ==========
    public static final String METHOD_AI = CmdInvokePayload.SCENARIO_AI;
    public static final String METHOD_SCREEN = CmdInvokePayload.SCENARIO_SCREEN;
    public static final String METHOD_DEBUG = CmdInvokePayload.SCENARIO_DEBUG;
    public static final String METHOD_SCRIPT = CmdInvokePayload.SCENARIO_SCRIPT;
    public static final String METHOD_LINKAGE = CmdInvokePayload.SCENARIO_LINKAGE;
    public static final String METHOD_APP = CmdInvokePayload.SCENARIO_APP;

    /** 设备标识 */
    private String uid;

    /** 设备名称 */
    @TableField(condition = SqlCondition.LIKE)
    private String deviceName;

    /** 指令配置（含指令名称/编码/参数等） */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject config;

    /** 执行状态：SUCCESS-成功 FAILURE-失败 */
    private String status;

    /** 结果描述 */
    private String resultMsg;

    /** 响应数据 */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject resultData;

    /** 执行时间(毫秒) */
    private Long elapsed;

    /** 下发方式：大屏/调试/脚本/场景联动/移动端 */
    private String method;

    /** 指令类型：控制指令/事件指令 */
    private String cmdType;

    /** 操作人 */
    private String operator;

    /** 操作人ID */
    private Long operatorId;

    /** 下发时间 */
    private Date createTime;

    public DeviceCmdLog putCmdName(String cmdName) {
        return this.putConfig("cmdName", cmdName);
    }

    public DeviceCmdLog putCmdParams(Object cmdParams) {
        return this.putConfig("cmdParams", cmdParams);
    }

    public DeviceCmdLog putConfig(String key, Object value) {
        if(this.config == null) {
            this.config = new JSONObject();
        }

        this.config.put(key, value);
        return this;
    }
}
