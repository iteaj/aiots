package com.iteaj.iboot.plugin.open.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 北向接入
 * </p>
 *
 * @author iteaj
 * @since 2025-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_northward")
public class Northward extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * mqtt_publish、
     * 接入类型
     */
    private String type;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 接入配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject config;

    /**
     * 创建时间
     */
    private Date createTime;

    private String connectKey;

    private String connectStatus;

    public static final String TYPE_MQTT="mqtt_publish";
    public static final String CONNECT_KEY="connect_key";
    public static final String CONNECT_STATUS="connect_status";

    public boolean isMqtt() {
        return TYPE_MQTT.equals(this.type);
    }
}
