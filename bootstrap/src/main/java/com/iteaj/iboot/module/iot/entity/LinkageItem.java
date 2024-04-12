package com.iteaj.iboot.module.iot.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@TableName("iot_linkage_item")
public class LinkageItem extends BaseEntity {

    /**
     * 所属场景
     */
    private Long linkageId;

    /**
     * 联动项名称
     */
    private String name;

    /**
     * 联动项说明
     */
    private String remark;

    /**
     * 操作的设备组
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray deviceGroupIds;

    /**
     * 场景执行动作
     */
    @TableField(exist = false)
    private List<LinkageAction> actions;

    /**
     * 场景执行条件
     */
    @TableField(exist = false)
    private List<LinkageCondition> conditions;

    public boolean equals(Object obj) {
        LinkageItem item = (LinkageItem) obj;
        return this.getId().equals(item.getId());
    }
}
