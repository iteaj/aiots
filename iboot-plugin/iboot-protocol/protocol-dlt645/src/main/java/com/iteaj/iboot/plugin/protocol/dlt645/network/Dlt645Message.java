package com.iteaj.iboot.plugin.protocol.dlt645.network;

import com.iteaj.iot.Message;

/**
 * ---------------------------------------------------------------------------------------------
 * 帧起始符     地址域       帧起始符     控制码     数据域长度    数据域           校验码           结束符
 *   68H   6个字节的BCD码    68H      占1个字节    占1个字节     数据     帧起始符到数据域之和求余     16H
 * ---------------------------------------------------------------------------------------------
 */
public interface Dlt645Message extends Message {

    @Override
    Dlt645MessageHead getHead();

    @Override
    Dlt645DtuMessageBody getBody();
}
