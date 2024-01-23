package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.framework.mybatis.handler.JacksonTypeHandler;
import com.iteaj.iboot.module.iot.consts.GatewayConnectType;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.consts.GatewayType;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 协议网关
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_gateway")
public class Gateway extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 网关名称
     */
    @NotBlank(message = "网关名称必填")
    private String name;

    /**
     * 网关类型(客户端、服务端)
     */
    private GatewayType type;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 网关端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 是否启用ssl
     */
    private Boolean useSsl;

    /**
     * 密码
     */
    private String password;

    /**
     * 绑定协议
     */
    @NotNull(message = "协议必填")
    private Long protocolId;

    /**
     * 网关状态
     */
    private GatewayStatus status;

    /**
     * 网关配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ObjectNode config;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 网关连接类型
     */
    private GatewayConnectType connectType;

    /**
     * 传输协议
     */
    @NotNull(message = "传输协议必填")
    private TransportProtocol protocolType;

}
