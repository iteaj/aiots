package com.iteaj.framework.result;

/**
 * create time: 2019/10/19
 *
 * @author iteaj
 * @since 1.0
 */
public class HttpResult<E> extends AbstractResult<E> {

    protected HttpResult(String message, int code) {
        super(message, code);
    }

    public HttpResult(E data, String message, int code) {
        super(data, message, code);
    }

    public static HttpResult Success(String message) {
        return Success(null, message);
    }

    /**
     * 默认使用状态码：200
     * @param message
     * @return
     */
    public static HttpResult Success(Object data, String message) {
        return HttpResult.StatusCode(data, message, 200);
    }

    public static HttpResult Fail(String message) {
        return Fail(null, message);
    }

    /**
     * 默认使用状态码：500
     * @param message
     * @return
     */
    public static HttpResult Fail(Object data, String message) {
        return HttpResult.StatusCode(data, message, 500);
    }

    public static HttpResult StatusCode(String message, int code) {
        return StatusCode(null, message, code);
    }

    /**
     *
     * @param message
     * @param code http状态码
     * @return
     */
    public static HttpResult StatusCode(Object data, String message, int code) {
        return new HttpResult(data, message, code);
    }
}
