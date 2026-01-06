package com.iteaj.iboot.plugin.script.model;

import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ValueConvertProvide;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.UpModelAttr;
import com.iteaj.framework.spi.iot.consts.DataType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class ModelAttrDataProvider extends ValueConvertProvide {

    public static List<ScriptComplete> completes;
    public static final ModelAttrDataProvider INSTANCE = new ModelAttrDataProvider();
    static {
        completes = new ArrayList<>(Arrays.asList(
                new ScriptComplete("methods.addRealtimeData(model, field, value)", "增加实时属性数据(field模型字段, value字段值)",1),
                new ScriptComplete("methods.isAttrField(model, field)", "是否为field的模型属性字段",1)
//                new ScriptComplete("methods.isProduct(model, productCode)", "是否为指定productCode的产品",1),
//                new ScriptComplete("methods.isProductField(model, productCode, field)", "是否为指定产品的指定字段",1)
        ));

        completes.addAll(VALUE_CONVERT_COMPLETES);
        completes.addAll(LOGGER_COMPLETES);
    }

    public boolean isAttrField(ModelAttrWrapper model, String field) {
        return model.getModel().getField().equals(field);
    }

    public void addRealtimeData(ModelAttrWrapper model, String field, Object value) {
        model.addRealtimeData(field, value);
    }

    public ModelAttrWrapper buildWrapper(UpModelAttr model) {
        return new ModelAttrWrapper(model);
    }

    @Getter
    @Setter
    public static class ModelAttrWrapper {
        private final UpModelAttr model;
        private Map<String, SignalOrFieldValue> fieldValues;

        public ModelAttrWrapper(UpModelAttr model) {
            this.model = model;
        }

        public void addRealtimeData(String field, Object value) {
            if(model.getDataType() != DataType.t_group) {
                throw new FrameworkException("模型属性非group类型, 不支持添加实时数据");
            }

            if(fieldValues == null)
                fieldValues = new HashMap<>();

            fieldValues.put(field, SignalOrFieldValue.build(null, field, value));
        }
    }
}
