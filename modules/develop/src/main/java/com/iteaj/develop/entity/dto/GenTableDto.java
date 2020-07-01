package com.iteaj.develop.entity.dto;

import com.iteaj.develop.entity.GenField;
import com.iteaj.develop.entity.GenTable;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenTableDto extends GenTable {

    /**
     * 要生成的文件
     * ENTITY、MAPPER、XML、SERVICE、SERVICE_IMPL、CONTROLLER、OTHER
     */
    private String[] writeType;
    private List<GenField> fieldList;

    public GenTableDto() {

    }

    public GenTableDto(String msn) {
        super(msn);
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

    public String[] getWriteType() {
        return writeType;
    }

    public GenTableDto setWriteType(String[] writeType) {
        this.writeType = writeType;
        return this;
    }
}
