package com.iteaj.iboot.plugin.protocol.{协议简称};

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

/**
 * {协议名称}事件接口实现
 * 
 * 【说明】
 * 1. 本类实现{协议名称}协议的事件接口
 * 2. 继承AbstractEventProtocolModelApi，用于平台定义的所有采集模型属性的设备接口
 * 3. 对应物模型中的"事件"类型，触发方式为active
 * 4. 事件 = 平台定义的所有采集模型属性的设备接口（不仅仅是设备主动上报）
 * 
 * 【事件的正确理解】
 * - 事件不是指设备主动上报的告警或状态变化
 * - 事件是平台定义的采集模型属性对应的设备接口
 * - 无论是主动采集(passive)还是被动接收(active)，都属于事件
 * - 例如：寄存器采集、设备数据上报、告警通知等都属于事件
 * 
 * 【事件接口 vs 功能接口】
 * - 功能接口(func): 平台主动发起的采集/控制请求，触发方式passive
 * - 事件接口(event): 平台定义的采集模型属性接口，触发方式active
 * - 两者都是事件，只是触发方式不同
 * 
 * 【事件的常见场景】
 * - 寄存器数据采集(如Modbus寄存器读取)
 * - 设备数据上报(如传感器定时上报)
 * - 告警通知(如温度超限告警)
 * - 状态变化(如开关状态变化)
 * - 设备响应(如对平台指令的响应)
 * 
 * 【开发要求】
 * ⚠️ 生成驱动时必须严格按照此模板文件
 * ⚠️ 所有方法必须保留，不得删除
 * ⚠️ 根据实际协议填充TODO标记的内容
 * ⚠️ 每个方法必须保留完整的Javadoc注释
 * 
 * @author {作者}
 * @since 1.0
 */
public class {协议简称}EventProtocolModelApi extends AbstractEventProtocolModelApi {

    /**
     * 构造函数
     * 
     * 【说明】
     * 1. 调用父类构造函数，传入接口代码、名称、说明
     * 2. 父类会自动设置type为ProtocolApiType.event
     * 3. 父类会自动设置triggerMode为TriggerMode.active
     * 
     * 【参数说明】
     * code - 接口代码，在协议内唯一，用于标识此事件
     * name - 接口名称，用于在管理端展示
     * remark - 接口说明，描述此事件的用途
     * 
     * 【事件的多样性】
     * 事件不仅仅指告警或状态变化，还包括：
     * - 寄存器采集事件：如读取温度寄存器、湿度寄存器
     * - 数据上报事件：如设备定时上报传感器数据
     * - 告警事件：如温度超限、设备离线
     * - 状态事件：如开关状态、运行状态
     * - 响应事件：如设备对平台指令的响应数据
     */
    public {协议简称}EventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
        
        // TODO: 如果需要配置上行字段，取消注释并配置
        // 事件接口通常只需要上行配置(设备→平台)
        // addUpConfig(new ProtocolModelApiConfig("alarmType", ApiConfigDirection.UP));
        // addUpConfig(new ProtocolModelApiConfig("alarmValue", ApiConfigDirection.UP));
    }

    /**
     * 构建协议报文
     * 
     * 
     * 【说明】
     * 1. 事件接口可能需要构建报文，也可能不需要
     * 2. 根据实际业务场景决定：
     *    - 寄存器采集：需要构建读取命令报文
     *    - 设备主动上报：通常返回null(平台被动接收)
     *    - 告警响应：可能需要构建确认报文
     * 
     * 【场景1：寄存器采集(需要构建报文)】
     * 例如：Modbus寄存器读取
     * <pre>
     * @Override
     * protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
     *     // 构建Modbus读寄存器命令
     *     byte[] command = buildReadCommand(arg.getField());
     *     return new DefaultProtocol(command);
     * }
     * </pre>
     * 
     * 【场景2：设备主动上报(返回null)】
     * 例如：设备定时上报传感器数据
     * <pre>
     * @Override
     * protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
     *     // 设备主动上报，平台被动接收
     *     return null;
     * }
     * </pre>
     * 
     * 【场景3：告警确认(需要构建报文)】
     * 例如：设备上报告警后，平台发送确认
     * <pre>
     * @Override
     * protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
     *     // 构建告警确认报文
     *     byte[] ackCommand = buildAlarmAckCommand(arg);
     *     return new DefaultProtocol(ackCommand);
     * }
     * </pre>
     * 
     * @param arg 调用参数
     * @return 协议报文对象，根据场景决定返回null或报文
     */
    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        // TODO: 事件接口通常返回null
        // 如果需要在事件触发时发送报文到设备，可在此构建
        
        // 示例：不需要发送报文
        return null;
    }

    /**
     * 执行协议通信
     * 
     * 【说明】
     * 1. 执行实际的设备通信或事件处理操作
     * 2. 根据事件类型不同，实现方式也不同：
     *    - 寄存器采集：发送读取命令，接收设备响应
     *    - 设备主动上报：处理接收到的数据，返回成功
     *    - 告警确认：发送确认报文到设备
     * 
     * 【场景1：寄存器采集(需要通信)】
     * <pre>
     * @Override
     * protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
     *     try {
     *         // 获取客户端
     *         XxxClient client = getClient(arg);
     *         // 发送读取命令
     *         byte[] response = client.sendAndReceive(protocol.getData());
     *         // 返回响应数据，由getDataSupplier解析
     *         result.accept(InvokeResult.success(response));
     *     } catch(Exception e) {
     *         result.accept(InvokeResult.fail(e.getMessage()));
     *     }
     * }
     * </pre>
     * 
     * 【场景2：设备主动上报(处理数据)】
     * <pre>
     * @Override
     * protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
     *     try {
     *         // 数据已在getDataSupplier中解析，这里直接返回成功
     *         result.accept(InvokeResult.success("事件已处理"));
     *     } catch(Exception e) {
     *         result.accept(InvokeResult.fail(e.getMessage()));
     *     }
     * }
     * </pre>
     * 
     * 【场景3：告警确认(发送确认)】
     * <pre>
     * @Override
     * protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
     *     try {
     *         // 发送告警确认报文
     *         XxxClient client = getClient(arg);
     *         client.send(protocol.getData());
     *         result.accept(InvokeResult.success("告警已确认"));
     *     } catch(Exception e) {
     *         result.accept(InvokeResult.fail(e.getMessage()));
     *     }
     * }
     * </pre>
     * 
     * 【数据流向】
     * 平台触发事件 → buildProtocol()构建报文(如果需要) → doInvoke()执行通信
     * → 设备响应/数据处理 → 回调结果 → getDataSupplier()解析数据(如果有响应)
     * 
     * @param protocol 协议报文(可能为null)
     * @param arg 调用参数
     * @param result 结果回调
     */
    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

        try {
            // TODO: 调用协议请求，根据实际需求实现
            // 场景1: 寄存器采集 - 发送读取命令，接收响应
            // XxxClient client = getClient(arg);
            // byte[] response = client.sendAndReceive(protocol.getData());
            // result.accept(InvokeResult.success(response));
            
            // 场景2: 设备主动上报 - 直接返回成功(数据在getDataSupplier中解析)
            result.accept(InvokeResult.success("事件已处理"));
            
            // 场景3: 告警确认 - 发送确认报文
            // XxxClient client = getClient(arg);
            // client.send(protocol.getData());
            // result.accept(InvokeResult.success("告警已确认"));
        } catch(Exception e) {
            result.accept(InvokeResult.fail(e.getMessage()));
        }
    }

    // ==================== 辅助方法(根据需要添加) ====================

    /**
     * 获取客户端实例
     * 
     * 【说明】
     * 1. 根据调用参数获取对应的客户端实例
     * 2. 用于需要与设备通信的场景(如寄存器采集、告警确认)
     * 
     * @param arg 调用参数
     * @return IoT客户端实例
     */
    // private XxxClient getClient(ProtocolModelApiInvokeParam arg) {
    //     // TODO: 实现获取客户端的逻辑
    //     // XxxProtocolSupplier supplier = (XxxProtocolSupplier) ProtocolSupplierManager.get(protocolCode);
    //     // return supplier.getClientByDeviceSn(arg.getDeviceSn());
    //     return null;
    // }

    /**
     * 构建读取命令
     * 
     * 【说明】
     * 1. 根据属性代码构建读取命令报文
     * 2. 用于寄存器采集等场景
     * 
     * @param field 属性代码
     * @return 读取命令报文
     */
    // private byte[] buildReadCommand(String field) {
    //     // TODO: 根据协议格式构建读取命令
    //     return new byte[]{};
    // }

    /**
     * 构建告警确认命令
     * 
     * 【说明】
     * 1. 构建告警确认报文
     * 2. 用于告警确认后发送给设备
     * 
     * @param arg 调用参数
     * @return 告警确认命令报文
     */
    // private byte[] buildAlarmAckCommand(ProtocolModelApiInvokeParam arg) {
    //     // TODO: 根据协议格式构建告警确认命令
    //     return new byte[]{};
    // }
}
