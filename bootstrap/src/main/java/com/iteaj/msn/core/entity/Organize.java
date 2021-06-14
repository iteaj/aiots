package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.TreeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("部门名称")
    private String name;
    @ApiModelProperty(hidden = true)
    private Integer sort;
    @ApiModelProperty("部门负责人手机号")
    private String phone;
    @ApiModelProperty("部门负责人")
    private String leader;
    @ApiModelProperty("部门路径")
    private String path;
    @ApiModelProperty(hidden = true)
    private Integer level;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

}
