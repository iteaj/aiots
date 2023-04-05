package com.iteaj.framework.spi.excel;

import java.io.Serializable;

public interface IExcelService {

    String toDictName(String dictType, Serializable dictValue);

    Serializable toDictValue(String dictType, String dictName);

    String toOrgName(Serializable id);

    Serializable toOrgValue(String name);
}
