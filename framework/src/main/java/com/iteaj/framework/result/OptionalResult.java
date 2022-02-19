package com.iteaj.framework.result;

import com.iteaj.framework.exception.ServiceException;

import java.util.Optional;
import java.util.function.Function;

/**
 * create time: 2019/7/26
 *  业务层操作结果
 * @author iteaj
 * @since 1.0
 */
public abstract class OptionalResult<E, _this extends AbstractResult<E>> extends AbstractResult<E> {

    public static long DCODE = HttpResult.DEFAULT_SUCCESS_CODE;
    public static String DMSG = HttpResult.DEFAULT_SUCCESS_MSG;

    protected OptionalResult(E value) {
        this(value, DMSG, DCODE);
    }

    public OptionalResult(E data, String message, long code) {
        super(data, message, code);
    }

    public Optional<E> of() {
        return Optional.of(getData());
    }

    public Optional<E> ofNullable() {
        return Optional.ofNullable(this.getData());
    }

    /**
     * @see Function#apply(Object) 直接返回响应的结果
     * @param function
     * @return
     */
    public _this build(Function<E, Result> function) {
        if(null == function) throw new ServiceException("参数错误");

        Result apply = function.apply(getData());
        return this.build(apply);
    }

    public _this build(Result result) {
        this.setCode(result.getCode());
        this.setMessage(result.getMessage());
        return (_this) this;
    }

    public _this build(long code, String message) {
        this.setCode(code);
        this.setMessage(message);
        return (_this) this;
    }

    @Override
    public _this setCode(long code) {
        return (_this) super.setCode(code);
    }

    @Override
    public _this setMessage(String message) {
        return (_this) super.setMessage(message);
    }

    @Override
    public _this setData(E data) {
        return (_this) super.setData(data);
    }
}
