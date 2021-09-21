package com.iteaj.framework.result;

import com.iteaj.util.IErrorCode;

import java.beans.Transient;

/**
 * create time: 2018/7/20
 *  Rest Api 错误码结果
 * @author iteaj
 * @version 1.0
 * @since JDK1.7
 */
public class ApiResult<E> extends AbstractResult<E> {

    private IErrorCode errorCode;

    public ApiResult(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ApiResult(E data, IErrorCode errorCode) {
        super(data, errorCode.getMsg(), errorCode.getCode());
        this.errorCode = errorCode;
    }

    @Transient
    public IErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(IErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
