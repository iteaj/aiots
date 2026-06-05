package com.iteaj.iboot.module.iot.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PanelsDeviceDto {

    /**
     * 控制设备状态控制项
     */
    private CtrlCommandDto ctrlCommand;

    /**
     * 控制值
     */
    private String ctrlValue;

    /**
     * 收集时间
     */
    private Date collectTime;

    /**
     * 各属性值
     */
    private List<AttrValue> values;

    /**
     * 设备
     */
    private DeviceDto device;

    public PanelsDeviceDto(CtrlCommandDto ctrlCommand, List<AttrValue> values, DeviceDto device) {
        this.ctrlCommand = ctrlCommand;
        this.values = values;
        this.device = device;
    }

    @Getter
    @Setter
    public static class AttrValue {
        /**
         * 属性字段
         */
        private String field;
        /**
         * 属性名称
         */
        private String name;
        /**
         * 属性单位
         */
        private String unit = "";

        /**
         * 属性最新值
         */
        private String value = "-";

        /**
         * 值标签
         */
        private String label = null;

        @JSONField(serialize = false)
        private Boolean ctrlStatus;

        public AttrValue(String field, String name, String unit, Boolean ctrlStatus) {
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.ctrlStatus = ctrlStatus;
        }
    }
}
