package com.iteaj.iboot.module.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PanelsDetailDto {

    /**
     * 设备信息
     */
    private Device device;

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

    public PanelsDetailDto addAttr(Long id, String field, String name, String unit, AttrType type) {
        return addAttr(id, field, name, unit, type, null, null, null);
    }

    public PanelsDetailDto addAttr(Long id, String field, String name, String unit, AttrType type, Object value, Date collectTime, CollectStatus status) {
        attrs.add(new ModelAttr(id, field, name, unit, value, type, collectTime, status));
        return this;
    }

    @Getter
    @Setter
    public static class ModelAttr {

        private final Long id;

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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date collectTime;

        private CollectStatus status;

        public ModelAttr(Long id, String field, String name, String unit, Object value
                , AttrType attrType, Date collectTime, CollectStatus status) {
            this.id = id;
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.value = value;
            this.status = status;
            this.attrType = attrType;
            this.collectTime = collectTime;
        }

        public String collectStatus() {
            return status == null ? "无" : status.getReason();
        }
    }

    public void addApiDebugParam(String apiCode, List<IVOption> params) {
        if(apiCodeDebugMap == null) {
            apiCodeDebugMap = new HashMap<>();
        }

        apiCodeDebugMap.put(apiCode, params);
    }
}
