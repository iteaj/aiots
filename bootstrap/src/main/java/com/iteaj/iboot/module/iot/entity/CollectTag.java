package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 采集标签
 * </p>
 *
 * @author iteaj
 * @since 2026-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_collect_tag")
public class CollectTag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /**
     * 标签编码
     */
    @NotBlank(message = "标签编码不能为空")
    private String code;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

}
