package com.iteaj.framework;

import lombok.Data;

@Data
public class ParamMeta {

    /**
     * 字段名
     */
    private String field;

    /**
     * 字段说明
     */
    private String label;

    /**
     * 额外说明
     */
    private String extra;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    public ParamMeta(String field, String label) {
        this.field = field;
        this.label = label;
    }

    public ParamMeta(String field, String label, Boolean required) {
        this.field = field;
        this.label = label;
        this.required = required;
    }

    public ParamMeta(String field, String label, String extra) {
        this.field = field;
        this.label = label;
        this.extra = extra;
    }

    public ParamMeta(String field, String label, String extra, Boolean required, Object defaultValue) {
        this.field = field;
        this.label = label;
        this.extra = extra;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public static ParamMeta build(String field) {
        return new ParamMeta(field, field);
    }

    public static ParamMeta build(String field, String label) {
        return new ParamMeta(field, label);
    }

    public static ParamMeta buildDefault(String field, String label, String defaultValue) {
        return buildDefault(field, label, null, defaultValue);
    }

    public static ParamMeta buildDefault(String field, String label, String extra, String defaultValue) {
        return new ParamMeta(field, label, extra, false, defaultValue);
    }

    public static ParamMeta build(String field, String label, String extra) {
        return new ParamMeta(field, label, extra);
    }

    public static ParamMeta buildRequired(String field, String label) {
        return new ParamMeta(field, label, true);
    }

    public static ParamMeta buildRequired(String field, String label, String extra, String defaultValue) {
        return new ParamMeta(field, label, extra, true, defaultValue);
    }
}
