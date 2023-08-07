package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 点位组
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_point_group")
public class PointGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 组名称
     */
    private String name;

    /**
     * 点位类型
     */
    @TableField(exist = false)
    private Long typeId;

    /**
     * 类型名称
     */
    @TableField(exist = false)
    private String typeName;

    /**
     * 型号名称
     */
    @TableField(exist = false)
    private String modelName;

    /**
     * 所属型号
     */
    private Integer modelId;

    /**
     * 点位信号列表
     */
    @TableField(exist = false)
    private List<String> signalIds;

    private Date createTime;

}
