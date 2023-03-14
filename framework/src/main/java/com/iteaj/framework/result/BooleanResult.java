package com.iteaj.framework.result;

/**
 * create time: 2019/7/26
 *
 * @author iteaj
 * @since 1.0
 */
public class BooleanResult extends OptionalResult<Boolean, BooleanResult> {

    public BooleanResult(Boolean value) {
        super(value);
    }

    public BooleanResult(Boolean value, String message) {
        super(value, message, value == true ? DCODE : HttpResult.DEFAULT_ERROR_CODE);
    }

    public BooleanResult(Boolean data, String message, long code) {
        super(data, message, code);
    }

    @Override
    public Boolean getData() {
        return super.getData() == null ? false : super.getData();
    }
}
