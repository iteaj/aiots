package com.iteaj.framework;

import com.iteaj.util.IErrorCode;

import java.io.Serializable;

public enum ErrorCode implements IErrorCode {
    OK(200, "成功")
    ,FAIL(500, "失败");

    private long code;
    private String msg;

    ErrorCode(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public Serializable getCode() {
        return code;
    }
}
