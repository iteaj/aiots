package com.iteaj.framework.spi.iot;

/**
 * 数据类型解析器
 */
public interface DataTypeResolver {

    Object resolver(UpModelAttr attr, Object value);
}
