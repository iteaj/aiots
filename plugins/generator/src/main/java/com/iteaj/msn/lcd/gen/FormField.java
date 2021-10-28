package com.iteaj.msn.lcd.gen;

import com.iteaj.msn.lcd.vue.FormType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormField {

    private String type;

    private String field;

    private String title;

    private boolean clear;

    private boolean required;

    private boolean disabled;

    private String extra;

    private String placeholder;

    private String dict;

    private String url;

    private FormType formType;

    private String component;
}
