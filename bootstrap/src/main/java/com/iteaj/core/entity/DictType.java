package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.core.enums.Status;
import com.iteaj.framework.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/7/13
 */
@Data
@ApiModel("字典类型")
@Accessors(chain = true)
@TableName("t_dict_type")
public class DictType extends BaseEntity {

    @ApiModelProperty("类型别名")
    private String type;

    @ApiModelProperty("类型名称")
    private String name;

    @ApiModelProperty("类型状态")
    private Status status;

    @ApiModelProperty("类型说明")
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
