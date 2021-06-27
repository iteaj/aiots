package com.iteaj.msn.dev.dto;

import com.iteaj.msn.dev.entity.GenField;
import com.iteaj.msn.dev.entity.GenTable;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenTableDto extends GenTable {

    private List<GenField> fieldList;

    public GenTableDto() {

    }

    public List<GenField> getFieldList() {
        return fieldList;
    }

    public GenTableDto setFieldList(List<GenField> fieldList) {
        this.fieldList = fieldList;
        return this;
    }

    public GenTableDto addGenField(GenField field) {
        if(null == fieldList) fieldList = new ArrayList<>();

        fieldList.add(field);

        return this;
    }
}
