package com.iteaj.framework.spi.iot.view.table;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableApiResult {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 当前分页记录
     */
    private List<?> records;

    public TableApiResult(List<?> records) {
        this(0L, records);
    }

    public TableApiResult(Long total, List<?> records) {
        this.total = total;
        this.records = records;
    }
}
