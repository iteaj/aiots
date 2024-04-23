package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.iboot.module.iot.consts.WarnGrade;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 告警记录
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_warn_record")
public class WarnRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 告警设备
     */
    private String uid;

    /**
     * 告警内容
     */
    private String content;

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 所属告警配置id
     */
    private Long warnConfigId;

    /**
     * 告警事件
     */
    private Date warnTime;

    private Date createTime;

    /**
     * 告警配置等级
     */
    @TableField(exist = false)
    private WarnGrade grade;

    /**
     * 所属产品
     */
    @TableField(exist = false)
    private Long productId;

    /**
     * 告警配置名称
     */
    @TableField(exist = false)
    private String warnConfigName;

    /**
     * 告警设备名称
     */
    @TableField(exist = false)
    private String deviceName;

    public WarnRecord() { }

    public WarnRecord(String uid) {
        this.uid = uid;
    }

    /**
     * 告警等级名称
     * @return
     */
    public String getGradeName() {
        return this.getGrade() != null ? this.getGrade().getDesc() : null;
    }
}
