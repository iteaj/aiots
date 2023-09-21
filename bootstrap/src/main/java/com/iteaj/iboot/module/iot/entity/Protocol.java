package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.iboot.module.iot.consts.CtrlMode;
import com.iteaj.iboot.module.iot.consts.ProtocolImplMode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 报文协议
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_protocol")
public class Protocol extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 协议名称
     */
    private String name;

    /**
     * 协议代码
     */
    private String code;

    /**
     * 协议类型
     */
    private String type;

    /**
     * 操控方式
     */
    private CtrlMode ctrlMode;

    /**
     * 实现类
     */
    private String implClass;

    /**
     * 实现方式
     */
    private ProtocolImplMode implMode;

    /**
     * jar文件路径
     */
    private String jarPath;

    /**
     * 协议备注
     */
    private String remark;

    /**
     * 校验类型
     */
    private String checkType;

    /**
     * 解码器类型
     */
    private String decoderType;

    /**
     * 解码器配置
     */
    private String decoderConfig;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public String getImplModeName() {
        return implMode != null ? implMode.getDesc() : null;
    }
}
