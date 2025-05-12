package com.iteaj.iboot.plugin.script.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.iteaj.framework.script.*;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaScriptImpl implements ScriptInterface<Source> {

    private static final String JS_ID = "js";
    Engine engine = Engine.newBuilder().allowExperimentalOptions(true)
            .option("engine.UsePreInitializedContext", "true")
            .build();

    @Override
    public ScriptLanguage getLanguage() {
        return ScriptLanguage.javascript;
    }

    @Override
    public Source compile(String script) {
        try {
            return Source.newBuilder(JS_ID, script, "test").mimeType("text/javascript").build();
        } catch (Exception e) {
            throw new ScriptExecException("编译失败["+e.getMessage()+"]", e);
        }
    }

    @Override
    public Object exec(String script, Map<String, Object> params) {
        try(Context compile = Context.newBuilder(JS_ID).engine(engine).allowAllAccess(true).build()) {
            Value bindings = compile.getBindings(JS_ID);
            if(params != null) {
                params.forEach(bindings::putMember);
            }
            Value eval = compile.eval(JS_ID, script);
            return getObject(eval);
        } catch (Exception e) {
            throw new ScriptExecException("脚本[JavaScript]执行异常", e);
        }
    }

    private static Object getObject(Value eval) {
        if(eval.hasArrayElements()) {
            return eval.as(List.class);
        } else if(eval.hasMembers()) {
            return JSON.toJSON(eval.as(Map.class));
        } else {
            if(eval.isBoolean()) {
                return eval.asBoolean();
            }
            return null;
        }
    }

    @Override
    public Object exec(ScriptContext context) {
        // 获取 JavaScript 脚本引擎
        long l = System.currentTimeMillis();
        try(Context compile = Context.newBuilder(JS_ID).engine(engine).allowAllAccess(true).build()) {
            Source source = (Source) ScriptManager.getCompilable(context.getKey());
            if(source != null) {
                Value bindings = compile.getBindings(JS_ID);
                if(context.getParams() != null) {
                    context.getParams().forEach(bindings::putMember);
                }

                compile.eval(source);
                Value eval = compile.eval(JS_ID, context.getInvoke());
                return getObject(eval);
            } else {
                return getObject(compile.eval(JS_ID, context.getScript()));
            }
        } catch (Exception e) {
            throw new ScriptExecException("脚本[JavaScript]执行异常["+context.getKey()+"]", e);
        } finally {
            System.out.println("执行时间 -- " + (System.currentTimeMillis()-l));
        }

    }
}
