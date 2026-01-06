package com.iteaj.iboot.plugin.iotview.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@TableName("isv_resource_type")
public class ResourceType extends BaseEntity {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 素材列表
     */
    @TableField(exist = false)
    List<Resource> resources;
}
