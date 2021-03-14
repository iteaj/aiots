package com.iteaj.modules.dev.dto;


import com.iteaj.modules.dev.entity.GenModule;
import com.iteaj.modules.dev.entity.GenTable;

import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenModuleDto extends GenModule {

    private List<GenTable> tables;

    public List<GenTable> getTables() {
        return tables;
    }

    public GenModuleDto setTables(List<GenTable> tables) {
        this.tables = tables;
        return this;
    }
}
