package com.iteaj.framework.spi.iot.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class DataItemOption {

    private String label;

    private Object key;

    private String url;

    private boolean isLeaf;

    private boolean disabled;

    private boolean unique;

    private List<DataItemOption> children;

    public DataItemOption(String label, Object key) {
        this.key = key;
        this.label = label;
    }

    public DataItemOption(String label, Object key, String url) {
        this.label = label;
        this.key = key;
        this.url = url;
    }

    public DataItemOption(String label, Object key, boolean isLeaf) {
        this.key = key;
        this.label = label;
        this.isLeaf = isLeaf;
    }

    public DataItemOption(String label, Object key, boolean isLeaf, boolean unique) {
        this.label = label;
        this.key = key;
        this.isLeaf = isLeaf;
        this.unique = unique;
    }

    public DataItemOption addChild(DataItemOption option) {
        if(children == null) {
            children = new ArrayList<>();
        }

        children.add(option);
        option.setKey(this.getKey()+":"+option.getKey());
        return this;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }
}
