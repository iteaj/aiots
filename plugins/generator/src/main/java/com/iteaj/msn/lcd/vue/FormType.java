package com.iteaj.msn.lcd.vue;

import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.iteaj.util.CommonUtils;

/**
 * form 表单类型
 */
public enum FormType {
    Date("<IvzDatePicker field=\"%s\" label=\"%s\" %s/>"),
    Time("<IvzTimePicker field=\"%s\" label=\"%s\" %s/>"),
    Month("<IvzMonthPicker field=\"%s\" label=\"%s\" %s/>"),
    DateTime("<IvzDatePicker field=\"%s\" label=\"%s\" show-time %s/>"),
    Input("<IvzInput field=\"%s\" label=\"%s\" %s/>"),
    Radio("<IvzRadio field=\"%s\" label=\"%s\" %s/>"),
    InputNumber("<IvzInputNumber field=\"%s\" label=\"%s\" %s/>"),
    Textarea("<IvzTextarea field=\"%s\" label=\"%s\" %s/>"),
    Dict("<IvzSelect field=\"%s\" label=\"%s\" %s/>"),
    ;

    private String template;

    FormType(String template) {
        this.template = template;
    }

    public static FormType getType(DbColumnType columnType, String comment) {
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
            case DATE_SQL:
            case LOCAL_DATE:
            case INSTANT:
                return Date;
            case TIMESTAMP:
            case LOCAL_DATE_TIME:
                return DateTime;
            case BOOLEAN:
            case BASE_BOOLEAN:
                return Radio;
            case CLOB:
                return Textarea;
            default:
                if(CommonUtils.isNotBlank(comment)) {
                    if(comment.contains("字典")) {
                        return Dict;
                    }
                }
                return Input;
        }
    }

    public String getTemplate(String... args) {
        return String.format(template, args);
    }
}
