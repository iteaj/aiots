package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 协议接口配置
 * </p>
 *
 * @author iteaj
 * @since 2023-09-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_protocol_api_config")
public class ProtocolApiConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 所属api接口
     */
    private Long protocolApiId;

    /**
     * 物模型属性id
     */
    private Long protocolAttrId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 配置开始位置
     */
    private Integer startIndex;

    /**
     * 配置长度
     */
    private Integer length;

    /**
     * 创建时间
     */
    private Date createTime;


}
