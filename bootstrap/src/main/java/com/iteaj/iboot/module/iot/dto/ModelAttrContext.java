package com.iteaj.iboot.module.iot.dto;

import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ModelAttrContext {

    private Map<String, String> dictValueMap;

    private Map<String, ModelAttrValue> values;

    public ModelAttrContext(Map<String, String> dictValueMap, Map<String, ModelAttrValue> values) {
        this.dictValueMap = dictValueMap;
        this.values = values;
    }

    public static ModelAttrValue of(ModelAttrMeta attrMeta) {
        ModelAttrValue value = new ModelAttrValue();
        value.setId(attrMeta.getId().toString()).setAttrType(attrMeta.getAttrType());
        value.setName(attrMeta.getName()).setUnit(attrMeta.getUnit());
        value.setField(attrMeta.getField());
        return value;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class ModelAttrValue implements Cloneable{

        /**
         *  属性id
         */
        private String id;
        /**
         *  属性名称
         */
        private String name;

        /**
         *  属性单位
         */
        private String unit;

        /**
         *  属性字段
         */
        private String field;
        /**
         *  属性值
         */
        private String value;

        /**
         *  属性值对应的标签
         */
        private String label;

        /**
         *  属性地址
         */
        private String address;

        /**
         *  告警等级
         */
        private String grade;

        /**
         *  属性类型
         */
        private AttrType attrType;

        /**
         *  告警指示
         */
        private String indicator;

        /**
         *  收集时间
         */
        private Date collectTime;

        @Override
        public ModelAttrValue clone() {
            try {
                return (ModelAttrValue) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
