package com.iteaj.framework.spi.iot.view.table;

import com.iteaj.framework.spi.iot.view.ApiConfigOption;
import lombok.Getter;

@Getter
public class TableApiConfig extends ApiConfigOption {

    @Override
    public TableApiConfig addReqParams(String name, String field, String placeholder) {
        return (TableApiConfig) super.addReqParams(name, field, placeholder);
    }

    public TableApiConfig addTableHead(String label, String value) {
        getResp().add(new TableHeadDesc(label, value));
        return this;
    }

    public TableApiConfig addTableHead(String label, String value, int width) {
        getResp().add(new TableHeadDesc(label, value, width));
        return this;
    }

    public TableApiConfig addTableHead(String label, String value, boolean ellipsis, boolean resizable) {
        getResp().add(new TableHeadDesc(label, value).setEllipsis(ellipsis).setResizable(resizable));
        return this;
    }

    public TableApiConfig addTableHead(String label, String value, int width, boolean resizable, boolean ellipsis) {
        return addTableHead(label, value, width, "center", ellipsis, resizable);
    }

    public TableApiConfig addTableHead(String label, String value, int width, String align, boolean resizable, boolean ellipsis) {
        getResp().add(new TableHeadDesc(label, value, width, align, resizable, ellipsis));
        return this;
    }

}
