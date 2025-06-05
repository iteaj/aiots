package com.iteaj.iboot.plugin.iotview.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 大屏项目
 * </p>
 *
 * @author iteaj
 * @since 2025-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("isv_project")
public class Project extends BaseEntity {

    private static final long serialVersionUID = 1L;

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


}
