package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.msn.core.enums.Status;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@TableName("t_role")
@Accessors(chain = true)
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     *
     */
    private Status status;
    private String remark;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
