package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.core.enums.Status;
import com.iteaj.framework.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
@Data
@ApiModel
@TableName("t_dict_data")
public class DictData extends BaseEntity {

    private Integer sort;
    private String label;
    private String value;
    private String type;
    private Status status;
    private String remark;
    private boolean izDefault;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
