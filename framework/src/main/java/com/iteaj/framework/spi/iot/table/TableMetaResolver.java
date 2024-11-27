package com.iteaj.framework.spi.iot.table;

import com.iteaj.iot.tools.annotation.IotField;
import com.iteaj.iot.tools.annotation.IotFieldMeta;
import com.iteaj.iot.tools.annotation.IotTableId;
import com.iteaj.iot.tools.annotation.IotTableIdMeta;
import com.iteaj.iot.tools.db.FieldMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableMetaResolver {

    public static List<FieldMeta> resolveEntityTypeToMetas(Class<?> entityClass) {
        List<FieldMeta> fieldMetas = new ArrayList<>();
        for (Field item : entityClass.getDeclaredFields()) {
            if(!item.isAccessible()) {
                item.setAccessible(true);
            }

            IotTableId fieldId = item.getAnnotation(IotTableId.class);
            if(fieldId != null) {
                fieldMetas.add(0, new IotTableIdMeta(fieldId, item));
            }

            IotField field = item.getAnnotation(IotField.class);
            if(field != null) {
                fieldMetas.add(new IotFieldMeta(field, item));
            }
        }

        return fieldMetas;
    }
}
