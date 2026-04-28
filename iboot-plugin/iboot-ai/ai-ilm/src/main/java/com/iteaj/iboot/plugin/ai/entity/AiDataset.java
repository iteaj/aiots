package com.iteaj.iboot.plugin.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.alibaba.fastjson2.JSONObject;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AI数据集主表数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_dataset")
public class AiDataset extends BaseEntity {

    /** 数据集名称 */
    @TableField(value = "name", condition = SqlCondition.LIKE)
    private String name;

    /** 数据集类型（db/image/text） */
    @TableField("type")
    private String type;

    /** 拓展配置JSON(splitRatio/dataSource/tableName等) */
    @TableField(value = "config", typeHandler = FastjsonTypeHandler.class)
    private JSONObject config;

    /** 总数据条数 */
    @TableField("record_count")
    private Long recordCount;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 创建时间 */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
