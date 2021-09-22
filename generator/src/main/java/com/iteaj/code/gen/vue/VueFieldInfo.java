package com.iteaj.code.gen.vue;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import lombok.Data;

@Data
public class VueFieldInfo {

    private String component;
    private TableField tableField;
    private String searchComponent;

    public VueFieldInfo(TableField tableField) {
        this.tableField = tableField;
    }

    public VueFieldInfo build() {
        DbColumnType columnType = (DbColumnType) tableField.getColumnType();
        this.component = FormType.getType(columnType)
                .getTemplate(getField(), getLabel(), getExact());

        if(isSearchComponent()) {
            this.searchComponent = this.component;
        }
        return this;
    }

    private String getField() {
        return tableField.getPropertyName();
    }

    private String getExact() {
        return "";
    }

    private String getLabel() {
        return tableField.getComment();
    }

    private boolean isSearchComponent() {
        return false;
    }

//    protected
}
