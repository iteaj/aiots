package com.iteaj.framework.spi.iot.view;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ApiConfigOption {

    /**
     * 请求参数列表
     */
    private List<ApiConfigDesc> req = new ArrayList<>();

    /**
     * 响应参数列表
     */
    private List<ApiConfigDesc> resp = new ArrayList<>();

    public ApiConfigOption addReqParams(String label, String value, String placeholder) {
        req.add(new ApiConfigDesc(label, value, placeholder));
        return this;
    }

    public ApiConfigOption addRespParams(String label, String value, String placeholder) {
        resp.add(new ApiConfigDesc(label, value, placeholder));
        return this;
    }
}
