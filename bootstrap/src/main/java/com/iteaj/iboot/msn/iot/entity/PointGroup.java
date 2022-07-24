package com.iteaj.iboot.msn.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
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
     * 所属型号
     */
    private Integer modelId;

    private Date createTime;


}
