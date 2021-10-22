package com.iteaj.msn.lcd.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.iteaj.framework.BaseEntity;
import java.util.Date;

import com.iteaj.framework.mybatis.handler.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 低代码功能设计
 * </p>
 *
 * @author iteaj
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("lcd_design")
public class LcdDesign extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 功能名称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    /**
     * 表明
     */
    @TableField(condition = SqlCondition.LIKE)
    private String tableName;

    /**
     * 表说明
     */
    private String comment;

    /**
     * 编辑组件配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ContainerNode edit;

    /**
     * 编辑组件配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ContainerNode search;

    /**
     * 表组件配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ContainerNode formTable;

    /**
     * 全局组件配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ContainerNode global;

    /**
     * 容器组件配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ContainerNode container;

    /**
     * sql脚本
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ContainerNode sqlScript;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;

}
