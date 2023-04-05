package com.iteaj.framework.spi.excel;

import java.io.Serializable;

public interface ExcelDict {

    String toName(Object entity);

    Serializable toValue(Object entity);
}
