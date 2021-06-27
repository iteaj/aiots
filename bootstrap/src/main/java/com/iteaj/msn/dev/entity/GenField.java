package com.iteaj.msn.dev.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@TableName("t_gen_filed")
@ApiModel("生成器字段实体")
public class GenField extends BaseEntity {

    @ApiModelProperty(value = "字段名", example = "用户名")
    private String name;
    @ApiModelProperty(value = "字段类型", example = "String")
    private String type;
    private String ctype;
    @ApiModelProperty(value = "字段标题", example = "用户名")
    private String title;
    @ApiModelProperty("字段注释")
    private String remark;
    @ApiModelProperty("表字段说明")
    private String comment;
    @ApiModelProperty("字段类型")
    private String dictType;
    private boolean keyFlag;
    private String validate;
    private String tableName;
    private String columnType;
    private Boolean editFlag;
    private Boolean searchFlag;
    private String propertyName;
    private boolean identityFlag;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    public GenField() {

    }

    public GenField(boolean identityFlag, String columnType) {
        this.identityFlag = identityFlag;
        this.columnType = columnType;
    }

}
