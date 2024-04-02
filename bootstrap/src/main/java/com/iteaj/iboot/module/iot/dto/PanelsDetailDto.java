package com.iteaj.iboot.module.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.consts.AttrType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PanelsDetailDto {

    /**
     * 设备信息
     */
    private DeviceDto device;

    /**
     * 产品信息
     */
    private ProductDto product;

    /**
     * 模型属性列表
     */
    private List<ModelAttr> attrs = new ArrayList<>();

    /**
     * 接口调试参数
     */
    private Map<String, List<IVOption>> apiCodeDebugMap;

    public PanelsDetailDto addAttr(String field, String name, String unit, AttrType type) {
        return addAttr(field, name, unit, type, null, null);
    }

    public PanelsDetailDto addAttr(String field, String name, String unit, AttrType type, Object value, Date collectTime) {
        attrs.add(new ModelAttr(field, name, unit, value, type, collectTime));
        return this;
    }

    @Getter
    @Setter
    public class ModelAttr {

        /**
         * 属性代码
         */
        private String field;

        /**
         * 属性名称
         */
        private String name;

        /**
         * 单位
         */
        private String unit;

        /**
         * 实时值
         */
        private Object value;

        /**
         * 读写方式
         */
        private AttrType attrType;

        /**
         * 采集时间
         */
        @JsonFormat(pattern = "MM-dd HH:mm:ss")
        private Date collectTime;

        public ModelAttr(String field, String name, String unit, Object value, AttrType attrType, Date collectTime) {
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.value = value;
            this.attrType = attrType;
            this.collectTime = collectTime;
        }
    }

    public void addApiDebugParam(String apiCode, List<IVOption> params) {
        if(apiCodeDebugMap == null) {
            apiCodeDebugMap = new HashMap<>();
        }

        apiCodeDebugMap.put(apiCode, params);
    }
}
