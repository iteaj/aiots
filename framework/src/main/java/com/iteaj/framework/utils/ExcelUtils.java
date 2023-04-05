package com.iteaj.framework.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public final class ExcelUtils {

    public static void exportForXSSF(Collection<?> records, Class<?> entityClass, OutputStream outputStream) throws IOException {
        final ExportParams exportParams = new ExportParams();
        final Workbook sheets = ExcelExportUtil.exportExcel(exportParams, entityClass, records);
        sheets.write(outputStream); outputStream.flush(); outputStream.flush();
    }

    public static void exportForHSSF(Collection<?> records, Class<?> entityClass, OutputStream outputStream) throws IOException {
        final ExportParams exportParams = new ExportParams();
        final Workbook sheets = ExcelExportUtil.exportExcel(exportParams, entityClass, records);
        sheets.write(outputStream); outputStream.flush(); outputStream.flush();
    }

    public static void exportExcel(Collection<?> records, Class<?> entityClass, ExportParams params, OutputStream outputStream) throws IOException {
        Workbook sheets = ExcelExportUtil.exportExcel(params, entityClass, records);
        sheets.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
