package com.iteaj.framework.script;

import com.iteaj.framework.exception.FrameworkException;

public class ScriptExecException extends FrameworkException {

    public ScriptExecException(String message) {
        super(message);
    }

    public ScriptExecException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptExecException(Throwable cause) {
        super(cause);
    }

    public ScriptExecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
