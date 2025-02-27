package com.iteaj.iboot.plugin.le5le.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Dataset2d {

    private String id;

    private String name;

    private List<Dataset2d> children;

    public Dataset2d(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Dataset2d(String id, String name, List<Dataset2d> children) {
        this(id, name);
        this.children = children;
    }
}
