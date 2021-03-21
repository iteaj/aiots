package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.core.enums.OrgType;
import com.iteaj.core.enums.Status;
import com.iteaj.framework.TreeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * create time: 2019/11/26
 *  组织机构实体
 * @author iteaj
 * @since 1.0
 */
@Data
@ApiModel
@TableName("t_organize")
@Accessors(chain = true)
public class Organize extends TreeEntity {

    private String name;
    private Integer sort;
    private String phone;
    private String email;
    private String leader;
    private Integer level;
    private Status status;
    private OrgType type;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

}
