package com.iteaj.iboot.module.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class EMapDetailDto {

    /**
     * 模型属性列表
     */
    private List<ModelAttr> attrs = new ArrayList<>();

    /**
     * 接口调试参数
     */
    private Map<String, List<IVOption>> apiCodeDebugMap;

    public EMapDetailDto addAttr(String field, String name, String unit) {
        return addAttr(field, name, unit, null, null, CollectStatus.Success);
    }

    public EMapDetailDto addAttr(String field, String name, String unit, Object value, Date collectTime, CollectStatus status) {
        attrs.add(new ModelAttr(field, name, unit, value, collectTime, status));
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
         * 采集时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date collectTime;

        private CollectStatus status;

        public ModelAttr(String field, String name, String unit, Object value
                , Date collectTime, CollectStatus status) {
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.value = value;
            this.status = status;
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
