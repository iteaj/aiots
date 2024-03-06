package com.iteaj.framework;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * 前端框架ivzone使用的option数据格式
 */
@Data
@Accessors(chain = true)
public class IVOption {

    /**
     * 标签
     */
    private String label;

    /**
     * 值
     */
    private Object value;

    /**
     * 是否禁用
     */
    private boolean disabled;

    /**
     * 是否默认选中
     */
    private boolean checked;

    /**
     * 子项
     */
    private List<IVOption> children;

    /**
     * 额外扩展信息
     */
    private Object extra;

    /**
     * 可选中
     */
    private Boolean selectable;

    /**
     * 复选框是否能被选中
     */
    private Boolean checkable;

    /**
     * 配置项
     */
    private Map<String, Object> config;

    public IVOption() {}

    public IVOption(String label, Object value) {
        this(label, value, null);
    }

    public IVOption(String label, Object value, Object extra) {
        this(label, value, false, extra);
    }

    public IVOption(String label, Object value, boolean disabled) {
        this(label, value, disabled, null);
    }

    public IVOption(String label, Object value, boolean disabled, Object extra) {
        this.label = label;
        this.value = value;
        this.extra = extra;
        this.disabled = disabled;
    }

    /**
     * 增加子项
     * @param label
     * @param value
     * @return
     */
    public IVOption addChildren(String label, Object value) {
        return this.addChildren(label, value, false);
    }

    /**
     * 增加子项
     * @param label
     * @param value
     * @param extra
     * @return
     */
    public IVOption addChildren(String label, Object value, Object extra) {
        if(children == null) {
            children = new ArrayList<>();
        }

        children.add(new IVOption(label, value, extra));
        return this;
    }

    /**
     * 增加子项
     * @param label
     * @param value
     * @param disabled
     * @return
     */
    public IVOption addChildren(String label, Object value, boolean disabled) {
        if(children == null) {
            children = new ArrayList<>();
        }

        children.add(new IVOption(label, value, disabled));
        return this;
    }

    /**
     * 增加配置项
     * @param key
     * @param value
     * @return
     */
    public IVOption addConfig(String key, Object value) {
        if(getConfig() == null) {
            this.config = new HashMap<>();
        }

        this.config.put(key, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(this.getValue(), ((IVOption)o).getValue());
    }

}
