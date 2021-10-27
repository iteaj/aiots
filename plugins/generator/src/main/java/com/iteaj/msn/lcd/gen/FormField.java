package com.iteaj.msn.lcd.gen;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormField {

    private String formType;

    private String field;

    private String title;

    private boolean clear;

    private boolean required;

    private boolean disabled;

    private String extra;

    private String placeholder;

    private String dict;

    private String url;
}
