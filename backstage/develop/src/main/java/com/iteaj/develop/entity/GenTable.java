package com.iteaj.develop.entity;

import com.iteaj.develop.enums.GenStatus;
import com.iteaj.framework.BaseEntity;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenTable extends BaseEntity {

    private String name;
    private String prefix;
    private String remark;
    private String comment;
    private String xmlName;
    private String viewType;
    private GenStatus status;
    private Boolean disabled;
    private String entityName;
    private String mapperName;
    private String serviceName;
    private String controllerName;
    private String serviceImplName;

    public GenTable() { }

    public GenTable(GenStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public GenTable setName(String name) {
        this.name = name;
        return this;
    }

    public String getXmlName() {
        return xmlName;
    }

    public GenTable setXmlName(String xmlName) {
        this.xmlName = xmlName;
        return this;
    }

    public String getMapperName() {
        return mapperName;
    }

    public GenTable setMapperName(String mapperName) {
        this.mapperName = mapperName;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public GenTable setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getControllerName() {
        return controllerName;
    }

    public GenTable setControllerName(String controllerName) {
        this.controllerName = controllerName;
        return this;
    }

    public String getServiceImplName() {
        return serviceImplName;
    }

    public GenTable setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public GenTable setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public GenTable setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public GenTable setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getViewType() {
        return viewType;
    }

    public GenTable setViewType(String viewType) {
        this.viewType = viewType;
        return this;
    }

    public GenStatus getStatus() {
        return status;
    }

    public GenTable setStatus(GenStatus status) {
        this.status = status;
        return this;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public GenTable setDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        GenTable genTable = (GenTable) obj;
        return genTable.getName().equals(this.getName());
    }

    public String getPrefix() {
        return prefix;
    }

    public GenTable setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
