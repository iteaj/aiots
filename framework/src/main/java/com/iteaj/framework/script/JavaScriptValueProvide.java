package com.iteaj.framework.script;

public interface JavaScriptValueProvide {

    default boolean isJSValue(Object value) {
        return false;
    }

    default boolean isJSArray(Object value) {
        return false;
    }

    default String jsArrayToString(Object value) {
        return null;
    }

    default String jsObjectToString(Object value) {
        return null;
    }

    default String jsMirrorToString(Object value) {


        return null;
    }
}
