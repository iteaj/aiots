package com.iteaj.framework.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes({JSONObject.class, JSONArray.class})
public class FastjsonTypeHandler extends AbstractJsonTypeHandler<JSON> {

    //  必须有这个构造方法
    public FastjsonTypeHandler(Class<?> type) {
        super(type);
    }

    @Override
    public JSON parse(String json) {
        return (JSON) JSON.parse(json);
    }

    @Override
    public String toJson(JSON obj) {
        return obj.toJSONString();
    }
}