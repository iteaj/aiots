package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.iboot.module.iot.collect.ProtocolApiType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 协议接口
 * </p>
 *
 * @author iteaj
 * @since 2023-09-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_protocol_api")
public class ProtocolApi extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 接口类型
     */
    private ProtocolApiType type;

    /**
     * 接口代码
     */
    private String code;

    /**
     * 所属协议
     */
    private Long protocolId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口状态
     */
    private String status;

    /**
     * 接口说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;


}
