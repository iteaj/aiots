package com.iteaj.iboot.plugin.iotview;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iteaj.framework.result.AbstractResult;
import com.iteaj.framework.result.HttpResult;
import com.iteaj.framework.result.Result;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class IotViewResult extends AbstractResult {

    public IotViewResult(Result result) {
        this(result.getData(), result.getMessage(), result.getCode());
    }

    protected IotViewResult(String message, long code) {
        super(message, code);
    }

    protected IotViewResult(Object data, String message, long code) {
        super(data, message, code);
    }

    public String getMsg() {
        return getMessage();
    }

    public static IotViewResult Success(Object data) {
        return new IotViewResult(data, "成功", HttpResult.DEFAULT_SUCCESS_CODE);
    }

    public static IotViewResult Success(Object data, String msg) {
        return new IotViewResult(data, msg, HttpResult.DEFAULT_SUCCESS_CODE);
    }

    public static IotViewResult Fail(String msg) {
        return Fail(msg, -HttpResult.DEFAULT_ERROR_CODE);
    }

    /**
     * 失败但前端不进行跳转失败页
     * @param msg
     * @return
     */
    public static IotViewResult FailNotJump(String msg) {
        return Fail(msg, -HttpResult.DEFAULT_ERROR_CODE);
    }

    public static IotViewResult Fail(String msg, int code) {
        return Fail(null, msg, code);
    }

    public static IotViewResult Fail(Object data, String msg, int code) {
        return new IotViewResult(data, msg, code);
    }
    @Override
    @JsonIgnore
    public String getMessage() {
        return super.getMessage();
    }
}
