package com.iteaj.framework.mybatis.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * create time: 2019/8/17
 * json里面的整型反序列化全部用Long类型
 * @author iteaj
 * @since 1.0
 */
@MappedTypes({JSONObject.class, JSONArray.class})
public class FastjsonLongTypeHandler extends FastjsonTypeHandler {

    public FastjsonLongTypeHandler(Class<?> type) {
        super(type);
    }

    public FastjsonLongTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        return JSON.parseObject(json, this.getFieldType(), JSONReader.Feature.UseLongForInts);
    }
}