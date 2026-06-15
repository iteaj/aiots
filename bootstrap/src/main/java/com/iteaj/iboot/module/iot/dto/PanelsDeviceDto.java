package com.iteaj.iboot.module.iot.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class PanelsDeviceDto {

    /**
     * 控制设备状态控制项
     */
    private List<CtrlCommandDto> ctrlCmds;

    /**
     * 控制值
     */
    private Map<String, String> ctrlValue;

    /**
     * 收集时间
     */
    private Date collectTime;

    /**
     * 各属性值
     */
    private List<ModelAttrContext.ModelAttrValue> values;

    /**
     * 设备
     */
    private DeviceDto device;

    public PanelsDeviceDto(List<CtrlCommandDto> ctrlCmds, List<ModelAttrContext.ModelAttrValue> values, DeviceDto device) {
        this.values = values;
        this.device = device;
        this.ctrlCmds = ctrlCmds;
    }

    public void putCtrlValue(String attrField, String string) {
        if(this.ctrlValue == null) {
            this.ctrlValue = new HashMap<>();
        }

        this.ctrlValue.put(attrField, string);
    }

    public int getCmdSize() {
        return this.ctrlCmds == null ? 0 : this.ctrlCmds.stream()
                .map(item -> item.getOptions().size())
                .reduce(0, Integer::sum);
    }

}
