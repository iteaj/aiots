package com.iteaj.iboot.msn.lcd.vue;

import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.iteaj.util.CommonUtils;

/**
 * form 表单类型
 */
public enum FormType {
    Date("<UDatePicker field=\"%s\" label=\"%s\" %s/>", false),
    Time("<UTimePicker field=\"%s\" label=\"%s\" %s/>", false),
    Month("<UMonthPicker field=\"%s\" label=\"%s\" %s/>", false),
    DateTime("<UDatePicker field=\"%s\" label=\"%s\" show-time %s/>", false),
    Input("<UInput field=\"%s\" label=\"%s\" %s/>", false),
    Radio("<URadio field=\"%s\" label=\"%s\" %s/>", true),
    Rate("<URate field=\"%s\" label=\"%s\" %s/>", false),
    Cascade("<UCascader field=\"%s\" label=\"%s\" %s/>", true),
    Checkbox("<UCheckbox field=\"%s\" label=\"%s\" %s/>", true),
    Slider("<USlider field=\"%s\" label=\"%s\" %s/>", false),
    Switch("<USwitch field=\"%s\" label=\"%s\" %s/>", false),
    InputNumber("<UInputNumber field=\"%s\" label=\"%s\" %s/>", false),
    Textarea("<UTextarea field=\"%s\" label=\"%s\" %s/>", false),
    Password("<UPassword field=\"%s\" label=\"%s\" %s/>", false),
    Dict("<USelect field=\"%s\" label=\"%s\" %s/>", true),
    Select("<USelect field=\"%s\" label=\"%s\" %s/>", true),
    Mentions("<UMentions field=\"%s\" label=\"%s\" %s/>", false),
    TreeSelect("<UTreeSelect field=\"%s\" label=\"%s\" %s/>", true),
    AutoComplete("<UAutoComplete field=\"%s\" label=\"%s\" %s/>", true),
    ;

    private String template;
    private boolean options;

    FormType(String template, boolean options) {
        this.options = options;
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

    public static FormType getType(String type) {
        switch (type) {
            case "text": return Input;
            case "radio": return Radio;
            case "rate": return Rate;
            case "checkbox": return Checkbox;
            case "slider": return Slider;
            case "select": return Select;
            case "switch": return Switch;
            case "password": return Password;
            case "textarea": return Textarea;
            case "cascade": return Cascade;
            case "stree": return TreeSelect;
            case "number": return InputNumber;
            case "mentions": return Mentions;
            case "autoComplete": return AutoComplete;
            default: return null;
        }
    }

    public String getTemplate(String... args) {
        return String.format(template, args);
    }

    public boolean isOptions() {
        return options;
    }
}
