package com.iteaj.code.gen.vue;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.iteaj.code.LowCodeProperties;
import com.iteaj.code.utils.LowCodeUtil;
import com.iteaj.util.CommonUtils;
import lombok.Data;

import java.util.Set;
import java.util.regex.Pattern;

@Data
public class VueFieldInfo {

    // 字段标题
    private String label;
    // 字段名
    private String field;
    // 表单组件
    private String component;
    // 是否必填
    private boolean required;
    // 表单类型
    private FormType formType;

    private TableField tableField;
    // 搜索表单组件
    private String searchComponent;

    public VueFieldInfo(TableField tableField) {
        this.tableField = tableField;
    }

    public VueFieldInfo build(LowCodeProperties properties) {
        DbColumnType columnType = (DbColumnType) tableField.getColumnType();
        this.label = getLabel();
        this.formType = FormType.getType(columnType);

        if(isEditComponent(properties)) {
            this.component = this.formType.getTemplate(this.getField(), this.getLabel(), this.getExact());
        }

        if(isSearchComponent(properties)) {
            this.searchComponent = this.formType.getTemplate(this.getField(), this.getLabel(), "");
        }

        return this;
    }

    public String getField() {
        return tableField.getPropertyName();
    }

    public boolean isRequired() {
        return tableField.getCustomMap() == null ? false : tableField.getCustomMap().get("null").equals("NO");
    }

    private String getExact() {
        if(tableField.getCustomMap() != null) {
            Object aDefault = tableField.getCustomMap().get("default");
            if(aDefault != null) {
                switch (this.formType) {
                    case Input:
                    case Textarea:
                        if(CommonUtils.isBlank((String) aDefault)) {
                            return "";
                        } else {
                            return "defaultValue=\""+aDefault+"\"";
                        }
                    case InputNumber:
                        return ":defaultValue=\""+aDefault+"\"";
                    default:
                        return "";
                }
            }
        }
        return "";
    }

    public String getLabel() {
        return LowCodeUtil.removeBracket(tableField.getComment());
    }

    private boolean isEditComponent(LowCodeProperties properties) {
        Set<String> exclude = properties.getForm().getEdit();
        if(exclude != null) {
            for(String item : exclude) {
                if(tableField.getName().equals(item)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isSearchComponent(LowCodeProperties properties) {
        String name = this.tableField.getName();
        Set<String> include = properties.getForm().getSearch();
        if(include != null && CommonUtils.isNotBlank(name)) {
            for(String item : include) {
                if(name.contains(item)) {
                    return true;
                }
            }
        }

        return false;
    }

}
