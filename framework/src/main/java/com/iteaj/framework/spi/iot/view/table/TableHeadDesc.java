package com.iteaj.framework.spi.iot.view.table;

import com.iteaj.framework.spi.iot.view.ApiConfigDesc;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@Accessors(chain=true)
public class TableHeadDesc extends ApiConfigDesc {

    /**
     *
     * 单元格宽度
     */
    private int width;

    /**
     * left、center、right
     * 列文本对齐方式
     */
    private String align;

    /**
     * 列是否可以拖动
     */
    private boolean resizable;

    /**
     * 文本溢出的处理方式
     */
    private boolean ellipsis;

    public TableHeadDesc(String label, String key) {
        this(label, key, 120);
    }

    public TableHeadDesc(String label, String key, int width) {
        this(label, key, width, "center", false, true);
    }

    public TableHeadDesc(String label, String key, String align) {
        this(label, key, 120, align, false, true);
    }

    public TableHeadDesc(String label, String key, int width, String align, boolean resizable, boolean ellipsis) {
        super(label, key, null);
        this.width = width;
        this.align = align;
        this.ellipsis = ellipsis;
        this.resizable = resizable;
    }

    public String getTitle() {
        return this.getLabel();
    }

    @Override
    @Transient
    public String getLabel() {
        return super.getLabel();
    }

    public String getKey() {
        return this.getValue();
    }

    @Override
    @Transient
    public String getValue() {
        return super.getValue();
    }

    @Override
    @Transient
    public String getPlaceholder() {
        return super.getPlaceholder();
    }
}
