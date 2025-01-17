package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.types.MemoryStream;

/**
 * 本系统的消息类，包含了各种解析规则，数据信息提取规则
 * The message class of this system contains various parsing rules and data information extraction rules
 */
public interface INetMessage {

    /**
     * 消息头的指令长度，第一次接受数据的长度
     * Instruction length of the message header, the length of the first received data
     */
    int ProtocolHeadBytesLength();

    /**
     * 从当前的头子节文件中提取出接下来需要接收的数据长度
     * Extract the length of the data to be received from the current header file
     * @return 返回接下来的数据内容长度
     */
    int GetContentLengthByHeadBytes();

    /**
     * 检查头子节的合法性
     * Check the legitimacy of the head subsection
     * @param token 特殊的令牌，有些特殊消息的验证
     * @return 是否合法的验证
     */
    boolean CheckHeadBytesLegal(byte[] token);

    /**
     * 在接收头报文的时候，返回前置无效的报文头字节长度，默认为0，不处理<br />
     * When receiving a header message, return the header byte length of the invalid header, the default is 0, and no processing is performed.
     * @param headByte 接收到的头子节信息
     * @return 头子节中无效的字节长度信息
     */
    int PependedUselesByteLength( byte[] headByte );

    /**
     * 获取头子节里的消息标识
     * Get the message ID in the header subsection
     * @return 消息标识
     */
    int GetHeadBytesIdentity();

    /**
     * 设置消息头子节
     * Set Message header byte
     * @param headBytes 字节数据
     */
    void setHeadBytes(byte[] headBytes);

    /**
     * 获取消息头字节
     * Get Message header byte
     * @return byte[]
     */
    byte[] getHeadBytes();

    /**
     * 获取消息内容字节
     * Get Message content byte
     * @return
     */
    byte[] getContentBytes();

    /**
     * 设置消息内容字节
     * Set Message content byte
     * @param contentBytes 内容字节
     */
    void setContentBytes(byte[] contentBytes);

    /**
     * 获取发送的消息
     * Get Byte information sent
     * @return byte[]
     */
    byte[] getSendBytes();

    /**
     * 设置发送的字节信息
     * Set Byte information sent
     * @param sendBytes 发送的字节信息
     */
    void setSendBytes(byte[] sendBytes);

    /**
     * 当消息头报文的长度定义为-1的时候，则使用动态的长度信息，可以使用本方法来判断一个消息是否处于完整的状态。<br />
     * If the length of the message header is defined as -1, this method can be used to determine whether a message is in the complete state by using dynamic length information.
     * @param send 发送的数据信息
     * @param ms 接收到数据内容
     * @return 是否是完整的消息
     */
    boolean CheckReceiveDataComplete( byte[] send, MemoryStream ms );

    /**
     * 检查发送的接收的报文是否是匹配的，如果匹配，则返回 1,  如果不匹配且直接返回错误，则返回 0，如果不匹配继续接收，直到匹配或是超时，则返回 -1<br />
     * If the packet is matched, 1 is returned. If the packet is not matched and an error is returned, 0 is returned. If the packet is not matched, -1 is returned until the packet is matched or times out
     *
     *  在某些协议里，存在一个消息id，发送方的消息id和返回的消息id是必须一致的。
     * @param send 当前发送的报文
     * @param receive 当前接收的报文信息
     * @return 如果匹配，则返回 1,  如果不匹配且直接返回错误，则返回 0，如果不匹配继续接收，直到匹配或是超时，则返回 -1
     */
    int CheckMessageMatch( byte[] send, byte[] receive );

}
