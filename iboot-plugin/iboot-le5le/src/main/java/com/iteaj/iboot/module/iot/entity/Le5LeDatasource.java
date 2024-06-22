package com.iteaj.iboot.module.iot.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.Entity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import lombok.Data;

import java.util.Date;

@Data
@TableName("iot_le5le_datasource")
public class Le5LeDatasource implements Entity<Long> {

    @TableId
    private transient Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型(subscribe、dataset、publish)
     */
    private String type;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject data;

    private Boolean shared;

    /**
     * 数据源id
     */
    private transient String datasourceId;

    /**
     * 设备组id
     */
    private transient Long deviceGroupId;

    @TableField(exist = false)
    private transient String deviceGroupName;

    /**
     * 创建时间
     */
    private transient Date createdAt;

    /**
     * 更新时间
     */
    private transient Date updateAt;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
