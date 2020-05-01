package com.iteaj.framework.gen;

import com.baomidou.mybatisplus.generator.config.po.TableField;

/**
 * create time: 2020/4/28
 *
 * @author iteaj
 * @since 1.0
 */
public class GenTableField extends TableField {

    private String title;
    private String ctype;
    private String dictType;
    private String validate;
    private Boolean searchFlag;

    public String getTitle() {
        return title;
    }

    public GenTableField setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCtype() {
        return ctype;
    }

    public GenTableField setCtype(String ctype) {
        this.ctype = ctype;
        return this;
    }

    public String getValidate() {
        return validate;
    }

    public GenTableField setValidate(String validate) {
        this.validate = validate;
        return this;
    }

    public Boolean getSearchFlag() {
        return searchFlag;
    }

    public GenTableField setSearchFlag(Boolean searchFlag) {
        this.searchFlag = searchFlag;
        return this;
    }

    public String getDictType() {
        return dictType;
    }

    public GenTableField setDictType(String dictType) {
        this.dictType = dictType;
        return this;
    }
}
