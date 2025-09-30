package com.iteaj.framework.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes({JSONObject.class, JSONArray.class})
public class FastjsonTypeHandler extends AbstractJsonTypeHandler<JSON> {
    // 必须调用父类构造器，指定泛型类型
    public FastjsonTypeHandler() {
        super(JSON.class);
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
