package com.iteaj.framework.json;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;

import java.lang.reflect.Type;

public class StringToMapDeserializer implements ObjectReader {
//    @Override
//    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
//        String currentName = jsonParser.getCurrentName();
//        if(jsonParser.currentToken() != JsonToken.VALUE_NULL) {
//            if(jsonParser.currentToken() == JsonToken.VALUE_STRING) {
//                Map<String, String> result = new HashMap<>();
//                String valueAsString = jsonParser.getValueAsString();
//                String[] items = valueAsString.split(",");
//                for (int i = 0; i < items.length; i++) {
//                    String item = items[i];
//                    String[] split = item.split("=");
//                    if(split.length != 2) {
//                        throw new IllegalArgumentException("错误的数据格式["+item+"]");
//                    }
//
//                    result.put(split[0], split[1]);
//                }
//
//                return result;
//            } else if(jsonParser.currentToken() == JsonToken.START_OBJECT){
//                return jsonParser.readValueAs(Map.class);
//            } else {
//                throw new InvalidFormatException(jsonParser, "数据格式异常", currentName, Map.class);
//            }
//        } else {
//            return null;
//        }
//    }

    @Override
    public Object readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        String string = jsonReader.getString();
        return null;
    }
}
