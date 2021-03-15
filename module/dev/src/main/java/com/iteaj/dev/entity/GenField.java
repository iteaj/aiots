package com.iteaj.dev.entity;

import com.iteaj.framework.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
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

    public GenField() {

    }

    public GenField(boolean identityFlag, String columnType) {
        this.identityFlag = identityFlag;
        this.columnType = columnType;
    }

    public String getName() {
        return name;
    }

    public GenField setName(String name) {
        this.name = name;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public GenField setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public String getType() {
        return type;
    }

    public GenField setType(String type) {
        this.type = type;
        return this;
    }

    public String getColumnType() {
        return columnType;
    }

    public GenField setColumnType(String columnType) {
        this.columnType = columnType;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public GenField setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isKeyFlag() {
        return keyFlag;
    }

    public GenField setKeyFlag(boolean keyFlag) {
        this.keyFlag = keyFlag;
        return this;
    }

    public boolean isIdentityFlag() {
        return identityFlag;
    }

    public GenField setIdentityFlag(boolean identityFlag) {
        this.identityFlag = identityFlag;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public GenField setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public GenField setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getCtype() {
        return ctype;
    }

    public GenField setCtype(String ctype) {
        this.ctype = ctype;
        return this;
    }

    public String getDictType() {
        return dictType;
    }

    public GenField setDictType(String dictType) {
        this.dictType = dictType;
        return this;
    }

    public String getValidate() {
        return validate;
    }

    public GenField setValidate(String validate) {
        this.validate = validate;
        return this;
    }

    public Boolean getSearchFlag() {
        return searchFlag;
    }

    public GenField setSearchFlag(Boolean searchFlag) {
        this.searchFlag = searchFlag;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public GenField setTitle(String title) {
        this.title = title;
        return this;
    }

    public Boolean getEditFlag() {
        return editFlag;
    }

    public GenField setEditFlag(Boolean editFlag) {
        this.editFlag = editFlag;
        return this;
    }
}
