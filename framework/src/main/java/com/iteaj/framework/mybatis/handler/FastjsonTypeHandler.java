package com.iteaj.framework.mybatis.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;

@MappedTypes({JSONObject.class, JSONArray.class})
public class FastjsonTypeHandler extends AbstractJsonTypeHandler<Object> {

    public FastjsonTypeHandler(Class<?> type) {
        super(type);
    }

    public FastjsonTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        return JSON.parseObject(json, this.getFieldType());
    }

    @Override
    public String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty);
    }
}
