package com.iteaj.code.gen.vue;

import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * form 表单类型
 */
public enum FormType {
    Date("<IvzDate field=\"%s\" label=\"%s\" picker=\"date\" %s/>"),
    Time("<IvzDate field=\"%s\" label=\"%s\" picker=\"time\" %s/>"),
    Month("<IvzInput field=\"%s\" label=\"%s\" picker=\"month\" %s/>"),
    Input("<IvzInput field=\"%s\" label=\"%s\" %s/>"),
    Radio("<IvzRadio field=\"%s\" label=\"%s\" %s/>"),
    InputNumber("<IvzNumber field=\"%s\" label=\"%s\" %s/>"),
    Textarea("<IvzTextarea field=\"%s\" label=\"%s\" %s/>"),
    ;

    private String template;

    FormType(String template) {
        this.template = template;
    }

    public static FormType getType(DbColumnType columnType) {
        switch (columnType) {
            case BYTE:
            case LONG:
            case FLOAT:
            case SHORT:
            case DOUBLE:
            case INTEGER:
            case BASE_INT:
            case BASE_BYTE:
            case BASE_LONG:
            case BASE_FLOAT:
            case BASE_DOUBLE:
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case BASE_SHORT:
                return InputNumber;
            case TIME:
            case LOCAL_TIME:
                return Time;
            case YEAR_MONTH:
                return Month;
            case DATE:
            case YEAR:
            case LOCAL_DATE:
            case DATE_SQL:
            case TIMESTAMP:
            case INSTANT:
            case LOCAL_DATE_TIME:
                return Date;
            case BOOLEAN:
            case BASE_BOOLEAN:
                return Radio;
            case CLOB:
                return Textarea;
            default:
                return Input;
        }
    }

    public String getTemplate(String... args) {
        return String.format(template, args);
    }
}
