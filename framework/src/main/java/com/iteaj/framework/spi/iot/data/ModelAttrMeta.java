package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.consts.AttrType;

import java.util.List;

public interface ModelAttrMeta {

    Long getId();

    Long getProductId();

    String getField();

    String getName();

    String getDataType();

    String getRealType();

    AttrType getAttrType();

    String getDefaultValue();

    String getUnit();

    Integer getAccuracy();

    Integer getGain();

    String getResolver();

    String getScript();

    String getRemark();

    List<ModelAttrDictMeta> getDictMetas();
}
