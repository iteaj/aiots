package com.iteaj.framework.spi.iot.view;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GlobalApiConfig {

    /**
     * 请求参数列表
     */
    private List<OptionDesc> req = new ArrayList<>();

    /**
     * 响应参数列表
     */
    private List<OptionDesc> resp = new ArrayList<>();

    public GlobalApiConfig addReqParams(String label, String value, String placeholder) {
        req.add(new OptionDesc(label, value, placeholder));
        return this;
    }

    public GlobalApiConfig addRespParams(String label, String value, String placeholder) {
        resp.add(new OptionDesc(label, value, placeholder));
        return this;
    }
    @Getter
    public static class OptionDesc {

        private String label;

        private String value;

        private String placeholder;

        public OptionDesc(String label, String value, String placeholder) {
            this.label = label;
            this.value = value;
            this.placeholder = placeholder;
        }
    }
}
