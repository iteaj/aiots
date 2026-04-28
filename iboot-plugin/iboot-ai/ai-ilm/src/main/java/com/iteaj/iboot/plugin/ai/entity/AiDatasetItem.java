package com.iteaj.iboot.plugin.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.alibaba.fastjson2.JSONObject;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AI数据集明细表数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_dataset_item")
public class AiDatasetItem extends BaseEntity {

    /** 关联主表ID */
    @TableField("dataset_id")
    private Long datasetId;

    /** 数据集项名称 */
    @TableField("name")
    private String name;

    /** 类型(db=数据库, image=图像, text=文本) */
    @TableField("type")
    private String type;

    /** 单个文件存储路径 */
    @TableField("file_path")
    private String filePath;

    /** 标签/分类 */
    @TableField("label")
    private String label;

    /** 数据集划分(train=训练集, val=验证集, test=测试集) */
    @TableField("split")
    private String split;

    /** 该文件包含的数据条数 */
    @TableField("record_count")
    private Long recordCount;

    /** 排序 */
    @TableField("sort")
    private Integer sort;

    /** 拓展配置JSON */
    @TableField(value = "config", typeHandler = FastjsonTypeHandler.class)
    private JSONObject config;

    /** 创建时间 */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
