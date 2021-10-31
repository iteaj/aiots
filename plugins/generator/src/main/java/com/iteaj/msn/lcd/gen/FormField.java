package com.iteaj.msn.lcd.gen;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormField {

    private String type;

    private String component;
}
