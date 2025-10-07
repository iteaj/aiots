package com.iteaj.framework.spi.iot.view;

import lombok.Getter;

@Getter
public class ApiConfigDesc {

    private String label;

    private String value;

    private String placeholder;

    public ApiConfigDesc(String label, String value, String placeholder) {
        this.label = label;
        this.value = value;
        this.placeholder = placeholder;
    }
}
