package com.iteaj.iboot.module.iot.dto;

import com.iteaj.framework.IVOption;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class PanelsDeviceDto {

    /**
     * 控制设备状态的属性列表
     */
    private List<IVOption> api;

    /**
     * 各属性值
     */
    private List<AttrValue> values;

    /**
     * 设备
     */
    private DeviceDto device;

    @Getter
    @Setter
    public class AttrValue {
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
        private String unit;

        /**
         * 属性最新值
         */
        private String value;
    }
}
