package com.iteaj.iboot.module.iot.entity;

import lombok.Data;

@Data
public class Dataset {

    private String id;

    private String type;

    private String label;

    private String mock;

    private String device;

    public Dataset() { }

    public Dataset(String id, String type, String label, String device) {
        this(id, type, label, device, null);
    }

    public Dataset(String id, String type, String label, String device, String mock) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.mock = mock;
        this.device = device;
    }
}
