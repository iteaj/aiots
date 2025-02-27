package com.iteaj.iboot.plugin.le5le.entity;

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
public class Le5LeDatasource implements Entity<String> {

    @TableId
    private String id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * dataset 数据集
     * 2d 2d图纸(组态)
     * 3d 3d图纸
     * dbs 大屏
     * 数据源类型(dataset、2d、3d、dbs)
     */
    private String type;

    /**
     * 封面
     */
    private String image;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject data;

    private Boolean shared;

    private Integer star;

    /**
     * 设备组id
     */
    private Long deviceGroupId;

    @TableField(exist = false)
    private String deviceGroupName;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updateAt;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
