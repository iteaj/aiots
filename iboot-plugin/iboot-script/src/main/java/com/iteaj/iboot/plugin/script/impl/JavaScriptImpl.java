package com.iteaj.iboot.plugin.script.impl;

import com.iteaj.framework.script.*;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.Map;

public class JavaScriptImpl implements ScriptInterface<Source> {

    private static final String JS_ID = "js";
    Engine engine = Engine.newBuilder().allowExperimentalOptions(true)
            .option("engine.WarnInterpreterOnly", "false")
            .option("engine.UsePreInitializedContext", "true")
            .build();

    @Override
    public ScriptLanguage getLanguage() {
        return ScriptLanguage.javascript;
    }

    @Override
    public Source compile(String script) {
        try (Context compile = newBuilder()) {
            Source source = Source.newBuilder(JS_ID, script, "test.js").mimeType("text/javascript").build();
            compile.parse(source);
            return source;
        } catch (Exception e) {
            throw new ScriptExecException("编译失败["+e.getMessage()+"]", e);
        }
    }

    private Context newBuilder() {
        return Context.newBuilder(JS_ID).engine(engine).allowAllAccess(true).build();
    }

    @Override
    public Object exec(String script, Map<String, Object> params) {
        try(Context compile = newBuilder()) {
            Value bindings = compile.getBindings(JS_ID);
            if(params != null) {
                params.forEach(bindings::putMember);
            }

            Value eval = compile.eval(JS_ID, script);
            return JavaScriptUtil.getValue(eval);
        } catch (Exception e) {
            throw new ScriptExecException("脚本[JavaScript]执行异常", e);
        }
    }

    @Override
    public Object exec(ScriptContext context) {
        // 获取 JavaScript 脚本引擎
        try(Context compile = newBuilder()) {
            Source source = (Source) ScriptManager.getCompilable(context.getKey());
            if(source != null) {
                Value bindings = compile.getBindings(JS_ID);
                if(context.getParams() != null) {
                    context.getParams().forEach(bindings::putMember);
                }

                compile.eval(source);
                Value eval = compile.eval(JS_ID, context.getInvoke());
                return JavaScriptUtil.getValue(eval);
            } else {
                return JavaScriptUtil.getValue(compile.eval(JS_ID, context.getScript()));
            }
        } catch (Exception e) {
            throw new ScriptExecException("脚本[JavaScript]执行异常["+context.getKey()+"]", e);
        }

    }
}
