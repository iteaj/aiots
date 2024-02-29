package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
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
    private Long uid;

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


}
