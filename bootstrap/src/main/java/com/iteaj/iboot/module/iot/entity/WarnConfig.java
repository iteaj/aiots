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
 * 告警配置
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_warn_config")
public class WarnConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 配置名称
     */
    private String name;

    /**
     * 告警等级
     */
    private WarnGrade grade;

    /**
     * 告警说明
     */
    private String remark;

    /**
     * 告警模板
     */
    private String template;

    /**
     * 所属产品
     */
    private Long productId;

    /**
     * 属性id
     */
    private Long modelAttrId;

    /**
     * 属性字段
     */
    private String modelAttrField;

    @TableField(exist = false)
    private String productName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 告警等级
     * @return
     */
    public String getGradeName() {
        return grade != null ? grade.getDesc() : null;
    }
}
