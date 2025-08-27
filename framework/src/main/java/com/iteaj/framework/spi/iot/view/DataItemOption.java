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

    private String value;

    private boolean isLeaf;

    private boolean disabled;

    /**
     * 选中为名称
     */
    private boolean name;

    /**
     * 全局接口配置, 必须放在此树形结构的倒数第二级, 用于声明最后一级的接口配置
     */
    private GlobalApiConfig config;
    private List<DataItemOption> children;
    public static final String SPLIT = ":";

    public DataItemOption() { }

    public DataItemOption(String label, String value) {
        this.value = value;
        this.label = label;
    }

    public DataItemOption(String label, String value, boolean isLeaf) {
        this.value = value;
        this.label = label;
        this.isLeaf = isLeaf;
    }

    public DataItemOption addChild(DataItemOption option) {
        return this.addChild(option, true);
    }


    public DataItemOption addChild(DataItemOption option, boolean split) {
        if(children == null) {
            children = new ArrayList<>();
        }

        children.add(option);
        if(split) {
            option.setValue(this.getValue() + SPLIT + option.getValue());
        } else {
            option.setValue(this.getValue() + "" + option.getValue());
        }

        return this;
    }

    public DataItemOption appendChild(DataItemOption option) {
        if(children == null) {
            children = new ArrayList<>();
        }

        children.add(option);
        return this;
    }

    public Builder builder() {
        return new Builder(this);
    }

    public DataItemOption asName() {
        this.name = true; return this;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    @Override
    public DataItemOption clone() throws CloneNotSupportedException {
        return (DataItemOption) super.clone();
    }

    public class Builder {
        public Builder parent;
        public DataItemOption current;

        protected Builder(DataItemOption current) {
            this.current = current;
        }

        public Builder(DataItemOption current, Builder parent) {
            this.parent = parent;
            this.current = current;
        }

        public Builder addChild(String label, String value) {
            final DataItemOption option = new DataItemOption(label, value);
            current.addChild(option);
            return new Builder(option, this);
        }

        public Builder addChild(String label, String value, GlobalApiConfig config) {
            final DataItemOption option = new DataItemOption(label, value);
            current.addChild(option.setConfig(config));
            return new Builder(option, this);
        }

        public Builder addChild(String label, String value, boolean isLeaf, GlobalApiConfig config) {
            final DataItemOption option = new DataItemOption(label, value, isLeaf);
            current.addChild(option.setConfig(config));
            return new Builder(option, this);
        }

        public Builder addNameChild(String label, String value) {
            final DataItemOption option = new DataItemOption(label, value).asName();
            current.addChild(option);
            return new Builder(option, this);
        }

        public Builder addChild(String label, String value, boolean isLeaf) {
            final DataItemOption option = new DataItemOption(label, value, isLeaf);
            current.addChild(option);
            return new Builder(option, this);
        }

        public Builder addNameChild(String label, String value, boolean isLeaf) {
            final DataItemOption option = new DataItemOption(label, value, isLeaf).asName();
            current.addChild(option);
            return new Builder(option, this);
        }

        public DataItemOption build() {
            return current;
        }

        public Builder toTop() {
            Builder temp = parent;
            do {
                if(temp.parent == null) {
                    return temp;
                }
            } while ((temp = temp.parent) != null);

            return null;
        }

        public Builder toParent() {
            return parent;
        }
    }
}
