package com.iteaj.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.iteaj.framework.FrameworkException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create time: 2021/7/4
 *
 * @author iteaj
 * @since 1.0
 */
public class JsonUtil {

    public static ObjectMapper mapper = new ObjectMapper();

    /**
     * java对象转json
     * @param obj   java对象
     * @return
     */
    public String toJson(Object obj){
        try {
            if(null == obj) {
                throw new IllegalArgumentException("参数错误");
            }

            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    /**
     * java对象转json
     * @param obj   java对象
     * @param format    日期格式化
     * @return
     */
    public String toJson(Object obj, SimpleDateFormat format){
        try {
            if(null == format)
                throw new IllegalArgumentException("[format]必填");

            return mapper.writer(format).writeValueAsString(obj);
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    /**
     * json转pojo对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T toBean(String json, Class<T> clazz){
        try {
            if(null == json || null == clazz) {
                throw new IllegalArgumentException("参数错误");
            }

            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    /**
     * json转List对象
     * @param json  json字符串
     * @param clazz   集合元素类型 eg: OrderBy.class
     * @param <T>
     * @return
     */
    public <T> List<T> toList(String json, Class<T> clazz){
        try {
            if(null == json || null == clazz) {
                throw new IllegalArgumentException("参数错误");
            }

            CollectionType type = getTypeFactory(mapper)
                    .constructCollectionType(ArrayList.class, clazz);

            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage(), e);
        }

    }

    /**
     * json转数组对象
     * @param json
     * @param arrayType   数组的类型
     * @return
     */
    public <T> T toArray(String json, Class<T> arrayType){
        if(!arrayType.isArray())
            throw new IllegalArgumentException("[arrayType]必须是数组类型eg：Integer[].class");
        return toBean(json, arrayType);
    }

    /**
     * json转成map
     * @param json
     * @param mapType   eg: HashMap.class
     * @param keyType   eg: String.class
     * @param valueType eg: OrderBy.class
     * @param <K>
     * @param <V>
     * @return
     */
    public <K,V> Map<K,V> toMap(String json, Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType){
        try {
            if(null == json || null == valueType) {
                throw new IllegalArgumentException("参数错误");
            }

            if(null == mapType || null == keyType || valueType == null)
                throw new IllegalArgumentException("必须指定Map的Key-Value类型");

            MapType mapType1 = getTypeFactory(mapper)
                    .constructMapType(mapType, keyType, valueType);

            return mapper.readValue(json, mapType1);
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    protected TypeFactory getTypeFactory(ObjectMapper objectMapper){
        return objectMapper.getTypeFactory();
    }

}
