package com.iteaj.iboot.module.iot.dto;

import com.alibaba.fastjson2.JSONObject;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 设备指令日志VO（移动端）
 */
@Data
@Accessors(chain = true)
public class DeviceCmdLogVo {

    /** 设备名称 */
    private String deviceName;

    /** 执行状态：SUCCESS-成功 FAILURE-失败 */
    private String status;

    /** 结果描述 */
    private String resultMsg;

    /** 执行时间(毫秒) */
    private Long elapsed;

    /** 下发方式 */
    private String method;

    /** 操作人 */
    private String operator;

    /** 下发时间 */
    private String createTime;

    /** 指令名称 */
    private String cmdName;

    public static DeviceCmdLogVo of(DeviceCmdLog log) {
        DeviceCmdLogVo vo = new DeviceCmdLogVo();
        vo.setDeviceName(log.getDeviceName());
        vo.setStatus(log.getStatus());
        vo.setResultMsg(log.getResultMsg());
        vo.setElapsed(log.getElapsed());
        vo.setMethod(log.getMethod());
        vo.setOperator(log.getOperator());
        vo.setCreateTime(log.getCreateTime() != null ? log.getCreateTime().toString() : null);

        // 从config中提取指令名称
        JSONObject config = log.getConfig();
        if (config != null && config.containsKey("cmdName")) {
            vo.setCmdName(config.getString("cmdName"));
        }

        return vo;
    }
}
