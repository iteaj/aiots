package com.iteaj.framework.authority;

import java.util.Arrays;

public class Result {

    public boolean isOk;
    public String message;

    public Result(boolean isOk, String message) {
        this.isOk = isOk;
        this.message = message;
    }

    public static Result success(String... message) {
        return new Result(true, message!=null
                ? Arrays.toString(message) : "OK");
    }

    public static Result fail(String... message) {
        return new Result(false, message!=null
                ? Arrays.toString(message) : "FAIL");
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
