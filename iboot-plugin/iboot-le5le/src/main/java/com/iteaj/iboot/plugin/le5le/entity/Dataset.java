package com.iteaj.iboot.plugin.le5le.entity;

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

    public void setType(String type) {
        if(type != null) {
            switch (type) {
                case "boolean":
                    this.type = "boolean";
                    break;
                case "int":
                case "byte":
                case "long":
                case "short":
                    this.type = "integer";
                    break;
                case "float":
                case "double":
                    this.type = "float";
                    break;
                case "array":
                    this.type = "array";
                    break;
                default:
                    this.type = "string";
                    break;
            }
        }
        this.type = type;
    }
}
