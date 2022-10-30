package com.iteaj.iboot.msn.iot.collect;

import lombok.Data;

@Data
public class CollectOption {

    private String label;

    private String value;

    public CollectOption(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
