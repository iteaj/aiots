package com.iteaj.framework.spi.iot.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class DataItemOption implements Cloneable{

    private String label;

    private Object value;

    private boolean load;

    private boolean isLeaf;

    private boolean disabled;

    private boolean unique;

    private List<DataItemOption> children;
    public static final String SPLIT = ":";

    public DataItemOption() { }

    public DataItemOption(String label, Object value) {
        this.value = value;
        this.label = label;
    }

    public DataItemOption(String label, Object value, boolean isLeaf) {
        this.value = value;
        this.label = label;
        this.isLeaf = isLeaf;
    }

    public DataItemOption(String label, Object value, boolean isLeaf, boolean unique) {
        this.label = label;
        this.value = value;
        this.isLeaf = isLeaf;
        this.unique = unique;
    }

    public DataItemOption addChild(DataItemOption option) {
        if(children == null) {
            children = new ArrayList<>();
        }

        children.add(option);
        return this;
    }

    public DataItemOption load() {
        this.load = true; return this;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    @Override
    public DataItemOption clone() throws CloneNotSupportedException {
        return (DataItemOption) super.clone();
    }
}
