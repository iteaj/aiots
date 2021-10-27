package com.iteaj.msn.lcd.gen;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormTableField {

    private String field;

    private String title;

    private double width;
}
