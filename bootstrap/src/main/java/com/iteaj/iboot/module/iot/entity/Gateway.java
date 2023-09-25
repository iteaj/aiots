package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.consts.GatewayType;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    private Long protocolId;

    /**
     * 传输协议
     */
    private TransportProtocol protocolType;

    /**
     * 网关状态
     */
    private GatewayStatus status;

    /**
     * 读写空闲(秒)
     */
    private Long allIdle;

    /**
     * 读空闲(秒)
     */
    private Long readIdle;

    /**
     * 写空闲(秒)
     */
    private Long writeIdle;

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


}
