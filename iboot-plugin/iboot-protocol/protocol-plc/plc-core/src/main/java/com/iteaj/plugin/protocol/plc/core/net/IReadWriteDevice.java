package com.iteaj.plugin.protocol.plc.core.net;

import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 用于读写的设备接口，相较于 {@link IReadWriteNet}，增加了 ReadFromCoreServer 相关的方法，可以用来和设备进行额外的交互。<br />
 */
public interface IReadWriteDevice extends IReadWriteNet {


    /**
     * 将当前的数据报文发送到设备去，具体使用什么通信方式取决于设备信息，然后从设备接收数据回来，并返回给调用者。<br />
     * Send the current data message to the device, the specific communication method used depends on the device information,
     * and then receive the data back from the device and return it to the caller.
     * @param send 发送的完整的报文信息
     * @return 接收的完整的报文信息
     */
    OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send );
}
