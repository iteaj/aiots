package com.iteaj.framework.utils;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.iteaj.framework.spi.excel.ExcelDictHandle;

public class ExcelExportParams extends ExportParams {

    @Override
    public IExcelDictHandler getDictHandler() {
        return ExcelDictHandle.getInstance();
    }
}
