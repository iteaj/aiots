package com.iteaj.framework.spi.iot.data;

import java.io.Serializable;
import java.util.Map;

public interface ModelAttrMetaService {

    Map<Serializable, ModelAttrMeta> getAttrMetas(Serializable productCode);

    default ModelAttrMeta getAttrMeta(Serializable productCode, Long modelAttrId) {
        Map<Serializable, ModelAttrMeta> attrMetas = getAttrMetas(productCode);
        if(attrMetas != null) {
            return attrMetas.get(modelAttrId);
        }

        return null;
    }

    void removeAttrMetas(Serializable productCode);
}
