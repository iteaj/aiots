package com.iteaj.framework.spi.iot.view;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ApiConfigOption {

    /**
     * 请求参数列表字段描述
     */
    private List<ApiConfigDesc> req = new ArrayList<>();

    /**
     * 响应参数列表字段描述
     */
    private List<ApiConfigDesc> resp = new ArrayList<>();

    public ApiConfigOption addReqParams(String name, String field, String placeholder) {
        req.add(new ApiConfigDesc(name, field, placeholder));
        return this;
    }

    public ApiConfigOption addRespParams(String name, String field, String placeholder) {
        resp.add(new ApiConfigDesc(name, field, placeholder));
        return this;
    }
}
