package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 场景联动
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_linkage")
public class Linkage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 场景名称
     */
    private String name;

    private Date createTime;

    /**
     * 所属事件源
     */
    private Long eventSourceId;


}
