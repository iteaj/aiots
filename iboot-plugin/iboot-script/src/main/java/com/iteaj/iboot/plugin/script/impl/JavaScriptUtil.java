package com.iteaj.iboot.plugin.script.impl;

import com.alibaba.fastjson2.JSON;
import org.graalvm.polyglot.Value;

import java.util.List;
import java.util.Map;

public class JavaScriptUtil {

    public static Object getValue(Value eval) {
        if(eval.hasArrayElements()) {
            return JSON.toJSON(eval.as(List.class));
        } else if(eval.hasMembers()) {
            return JSON.toJSON(eval.as(Map.class));
        } else if(eval.isNull()) {
            return null;
        } else {
            if(eval.isString()) {
                return eval.asString();
            } else if(eval.isDate()) {
                return eval.asDate();
            } else if(eval.isNumber()) {
                if(eval.fitsInInt()) {
                    return eval.asInt();
                } else if(eval.fitsInLong()) {
                    return eval.asLong();
                } else if(eval.fitsInDouble()) {
                    return eval.asDouble();
                } else if(eval.fitsInFloat()) {
                    return eval.asFloat();
                }
            } else if(eval.isBoolean()) {
                return eval.asBoolean();
            }

            return null;
        }
    }
}
