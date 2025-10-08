package com.iteaj.framework.spi.iot.view.table;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableApiResult {

    private Long total;

    private List<?> records;

    public TableApiResult(Long total, List<?> records) {
        this.total = total;
        this.records = records;
    }
}
