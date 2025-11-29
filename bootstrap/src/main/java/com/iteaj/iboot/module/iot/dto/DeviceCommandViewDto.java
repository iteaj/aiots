package com.iteaj.iboot.module.iot.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class DeviceCommandViewDto {

    private String label;

    private String value;

    private String code;

    private String remark;

    private boolean isLeaf;

    private List<ViewConfig> configs;

    public DeviceCommandViewDto(String label, String value, String code) {
        this.code = code;
        this.label = label;
        this.value = value;
    }

    public String getValue() {
        return String.format("api->%s%s", code, value == null ? "" : "&" + value);
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    @Transient
    public String getCode() {
        return code;
    }

    @Getter
    @Setter
    public static class ViewConfig {
        private String label;
        private String value;

        public ViewConfig(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
}
