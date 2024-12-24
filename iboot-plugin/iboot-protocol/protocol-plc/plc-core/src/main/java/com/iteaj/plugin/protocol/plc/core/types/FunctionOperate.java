package com.iteaj.plugin.protocol.plc.core.types;

/**
 * 一个匿名的委托对象，包含了一个action方法，将返回指定的类型<br />
 * An anonymous delegate object, including an action method, will return the specified type
 * @param <R> 返回的数据类型
 */
public class FunctionOperate<R>
{
    public R Action(){
        return null;
    }
}
