package com.iteaj.develop.entity;

import com.iteaj.framework.BaseEntity;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenField extends BaseEntity {

    private String msn;
    private String name;
    private String type;
    private String ctype;
    private String title;
    private String remark;
    private String comment;
    private String dictType;
    private boolean keyFlag;
    private String validate;
    private String tableName;
    private String columnType;
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

    public String getMsn() {
        return msn;
    }

    public GenField setMsn(String msn) {
        this.msn = msn;
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
}
