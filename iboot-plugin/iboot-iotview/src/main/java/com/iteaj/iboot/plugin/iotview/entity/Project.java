package com.iteaj.iboot.plugin.iotview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.Date;

/**
 * <p>
 * 大屏项目
 * </p>
 *
 * @author iteaj
 * @since 2025-06-04
 */
@Data
@Accessors(chain = true)
@TableName("isv_project")
public class Project implements Entity<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 项目封面
     */
    private String indexImage;

    /**
     * 项目说明
     */
    private String remarks;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 项目内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    public void setId(String id) {
        this.id = id;
    }
}
