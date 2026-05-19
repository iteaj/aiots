package com.iteaj.iboot.plugin.protocol.{协议简称};

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.iboot.plugin.protocol.{协议简称}.component.{协议简称}ClientComponent;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.data.ProtocolModelAttr;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * {协议名称}协议提供 - 客户端模式
 * 
 * 【说明】
 * 1. 本类是{协议名称}协议驱动的核心入口类
 * 2. 继承StaticClientProtocolSupplier实现静态客户端协议
 * 3. 适用于网关主动连接设备的场景(如Modbus TCP Client、OPC UA等)
 * 4. 必须添加@Component注解注册为Spring Bean
 * 
 * 【泛型参数说明】
 * C - 网络配置类，使用NetworkConfigImpl的子类(如TcpNetworkConfig、MqttNetworkConfig等)
 * T - 网络组件类，管理设备连接的组件
 * K - IoT客户端类，单个设备的具体连接实例
 * 
 * 【开发要求】
 * ⚠️ 生成驱动时必须严格按照此模板文件
 * ⚠️ 所有方法、属性、构造函数必须保留，不得删除
 * ⚠️ 根据实际协议填充TODO标记的内容
 * ⚠️ 每个方法必须保留完整的Javadoc注释
 * 
 * @author {作者}
 * @since 1.0
 */
@Component
public class {协议简称}ProtocolSupplier extends StaticClientProtocolSupplier<
        NetworkConfigImpl.{网络配置类型},           // TODO: 替换为实际的网络配置类型
        {协议简称}ClientComponent,                  // TODO: 替换为实际的组件类
        {协议简称}Client> {                         // TODO: 替换为实际的客户端类

    /**
     * 网关配置参数
     * 
     * 【说明】
     * 1. 定义在平台配置设备时需要填写的参数
     * 2. 用户在管理端添加设备时会看到这些配置项
     * 3. 根据协议实际需求配置，常见参数：host、port、timeout等
     * 
     * 【参数构建方法】
     * - ParamMeta.buildRequired(field, name) - 必填字段
     * - ParamMeta.buildRequiredNumber(field, name, defaultValue, remark) - 必填数字
     * - ParamMeta.buildNumber(field, name, defaultValue, remark) - 选填数字
     * - ParamMeta.buildSelect(field, name, defaultValue, options) - 下拉选择
     */
    private ParamMeta[] GatewayMetas = new ParamMeta[]{
            // TODO: 根据协议实际要求配置网关参数
            ParamMeta.buildRequired("host", "主机地址"),
            ParamMeta.buildRequiredNumber("port", "端口", {默认端口}, "端口说明"),
            ParamMeta.buildRequiredNumber("timeout", "请求超时(秒)", 5, "请求超时时间"),
            // ParamMeta.buildNumber("readIdle", "读空闲(秒)", 0, "小于等于0则不启用"),
    };

    /**
     * 构造函数
     * 
     * 【说明】
     * 1. 调用父类构造函数，传入协议码、传输协议类型、协议描述
     * 2. 协议码必须全局唯一，建议使用大写+下划线格式
     * 3. 传输协议类型参考TransportProtocol枚举(TCP/MQTT/UDP/HTTP/WEBSOCKET/SERIAL)
     * 
     * 【参数说明】
     * protocolCode - 协议码，全局唯一标识(如：MODBUS_TCP、OPC_UA等)
     * transportProtocol - 传输协议类型
     * description - 协议描述，说明此协议的用途
     */
    public {协议简称}ProtocolSupplier() {
        super("{协议码}", TransportProtocol.{传输协议类型}, "用于{协议用途描述}");
    }

    /**
     * 获取协议描述
     * 
     * 【说明】
     * 1. 返回协议的简短描述
     * 2. 用于在平台展示协议信息
     * 3. 建议简洁明了，如"Modbus Tcp协议提供"
     * 
     * @return 协议描述
     */
    @Override
    public String getDesc() {
        return "{协议名称}协议提供";
    }

    /**
     * 获取协议版本
     * 
     * 【说明】
     * 1. 返回协议的版本号
     * 2. 用于协议版本管理
     * 3. 建议格式：主版本.次版本.修订号(如：1.0.0)
     * 
     * @return 协议版本号
     */
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * 获取网关类型
     * 
     * 【说明】
     * 1. 返回网关类型(设备直连/边缘网关)
     * 2. 设备直连(Direct)：真正产生数据的设备直接连接平台
     *    - 示例：传感器、智能仪表等设备直接连接平台
     *    - 特点：设备本身就是数据源，直接与平台通信
     * 3. 边缘网关(Edge)：产生数据的设备通过边缘网关间接连接平台
     *    - 示例：PLC下挂载多个子设备，PLC本身不生产数据
     *    - 特点：平台通过连接网关(如PLC)读取子设备数据
     *    - 数据流向：子设备 → 边缘网关(PLC) → 平台
     * 4. 默认返回GatewayType.Direct(设备直连)
     * 
     * 【选择依据】
     * - 如果设备直接产生数据并上报 → Direct(设备直连)
     * - 如果设备作为网关采集下级设备数据 → Edge(边缘网关)
     * 
     * @return 网关类型
     */
    @Override
    public GatewayType getGatewayType() {
        return GatewayType.Direct;
    }

    /**
     * 获取实现方式
     * 
     * 【说明】
     * 1. 返回协议的实现方式(Internal/Jar)
     * 2. Internal：内置实现，代码在当前项目中
     * 3. Jar：外部Jar包实现，动态加载
     * 4. 默认返回ProtocolImplMode.Internal(内置实现)
     * 
     * @return 实现方式
     */
    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    /**
     * 获取协议模型
     * 
     * 【说明】
     * 1. 返回协议的完整模型定义
     * 2. 包含属性(点位)、功能接口、事件接口
     * 3. 此方法在协议注册时调用，用于初始化协议模型
     * 4. 必须实现，返回null会导致协议无法注册
     * 
     * 【实现步骤】
     * 1. 创建DefaultProtocolModel
     * 2. 添加协议属性(ProtocolModelAttr)
     * 3. 添加功能接口(createFuncApi)
     * 4. 添加事件接口(createEventApi)
     * 5. 返回协议模型
     * 
     * 【示例：Modbus TCP协议模型】
     * <pre>
     * @Override
     * public ProtocolModel getProtocol() {
     *     DefaultProtocolModel model = new DefaultProtocolModel(
     *         "MODBUS_TCP",           // 协议码
     *         CtrlMode.COMMON,        // 控制方式
     *         TransportProtocol.TCP   // 传输协议
     *     );
     *     
     *     // 添加属性
     *     model.addModelAttr(new ProtocolModelAttr(
     *         "temperature",          // 属性代码
     *         "温度",                 // 属性名称
     *         DataType.t_float,       // 数据类型
     *         AttrType.R              // 只读
     *     ));
     *     
     *     // 添加功能接口
     *     model.addModelApi(createFuncApi("read_temp", "读取温度", "读取温度寄存器"));
     *     
     *     // 添加事件接口
     *     model.addModelApi(createEventApi("temp_alarm", "温度告警", "温度超限告警"));
     *     
     *     return model;
     * }
     * </pre>
     * 
     * @return 协议模型
     */
    @Override
    public ProtocolModel getProtocol() {
        // TODO: 创建并返回协议模型
        DefaultProtocolModel model = new DefaultProtocolModel(
            "{协议码}",              // 协议码
            CtrlMode.COMMON,         // 控制方式
            TransportProtocol.{传输协议类型}  // 传输协议
        );
        
        // TODO: 添加协议属性
        // model.addModelAttr(new ProtocolModelAttr(
        //     "field_code",          // 属性代码
        //     "属性名称",            // 属性名称
        //     DataType.t_float,      // 数据类型
        //     AttrType.R             // 读写方式
        // ));
        
        // TODO: 添加功能接口
        // model.addModelApi(createFuncApi("read_data", "读取数据", "读取设备数据"));
        
        // TODO: 添加事件接口
        // model.addModelApi(createEventApi("data_report", "数据上报", "设备数据上报"));
        
        return model;
    }

    /**
     * 创建网络组件
     * 
     * 【说明】
     * 1. 创建并返回网络组件实例
     * 2. 网络组件负责管理底层网络连接(如TCP Server/Client、MQTT连接等)
     * 3. 此方法只在首次创建组件时调用
     * 4. 组件会被FrameworkManager自动注册和管理
     * 
     * 【实现要求】
     * - 创建并返回组件实例
     * - 不要在方法中启动连接，由Framework统一管理
     * - 如果组件需要特殊初始化，在此处完成
     * 
     * @param config 网络配置，包含host、port等配置信息
     * @return 网络组件实例
     */
    @Override
    protected {协议简称}ClientComponent doCreateComponent(NetworkConfigImpl.{网络配置类型} config) {
        // TODO: 创建并返回网络组件实例
        return new {协议简称}ClientComponent();
    }

    /**
     * 创建IoT客户端
     * 
     * 【说明】
     * 1. 为每个设备创建独立的客户端连接
     * 2. 当平台添加设备时，会调用此方法创建该设备的连接
     * 3. 客户端负责与具体设备进行通信
     * 4. 需要设置ConnectKey为设备编号(deviceSn)
     * 
     * 【实现要求】
     * - 从config中获取host、port等配置
     * - 创建ClientConnectProperties并配置参数
     * - 设置ConnectKey为config.getDeviceSn()
     * - 调用component.createNewClient()创建客户端
     * - 可配置超时时间、心跳间隔等参数
     * 
     * @param config 网络配置，包含设备连接信息
     * @param component 网络组件，用于创建客户端
     * @return IoT客户端实例
     */
    @Override
    protected {协议简称}Client doCreateClient(NetworkConfigImpl.{网络配置类型} config, {协议简称}ClientComponent component) {
        // TODO: 根据实际协议要求配置连接参数
        ClientConnectProperties connectProperties = new ClientConnectProperties(
                config.getHost(), 
                config.getPort()
        );
        
        // TODO: 如果需要读空闲检测，取消注释并配置
        // if(config.getReadIdle() != null && config.getReadIdle() > 0) {
        //     connectProperties.setReaderIdleTime(config.getReadIdle());
        // }
        
        // 必须设置ConnectKey为设备编号
        connectProperties.setConnectKey(config.getDeviceSn());
        
        // 创建并返回客户端
        return ({协议简称}Client) component.createNewClient(connectProperties);
    }

    /**
     * 获取协议提供的数据(核心方法)
     * 
     * 【说明】
     * 1. 这是协议驱动最核心的方法，负责解析设备报文数据
     * 2. 当设备数据到达时，框架会调用此方法解析数据
     * 3. 将原始报文转换为平台可理解的DataSupplier对象
     * 4. 此方法的实现质量直接决定驱动的数据采集能力
     * 
     * 【参数说明】
     * protocol - 协议报文对象，包含设备的具体协议数据
     *            - 可以转换为具体的协议对象(如ModbusProtocol、MqttProtocol等)
     *            - 包含协议的所有配置信息和数据
     *            - 通过protocol.getData()获取原始报文
     *            - 通过protocol.getXxx()获取协议特定数据
     * param - 调用参数，平台主动调用时传入，可能为null
     * value - 协议请求返回值，即设备响应的原始数据
     *           类型取决于网络层：byte[](TCP/UDP)、String(HTTP/WS)、JSONObject(MQTT)
     * 
     * 【返回说明】
     * DataSupplier - 采集数据容器
     * - 必须设置protocolCode和deviceKey
     * - 调用addValue(code, name, value)添加属性值
     * 
     * 【实现步骤】
     * 1. 将protocol转换为具体协议对象
     * 2. 从具体协议对象中获取协议数据
     * 3. 从value中获取原始报文数据
     * 4. 根据协议格式解析报文
     * 5. 提取各个属性的值
     * 6. 构建DataSupplier对象
     * 7. 调用addValue()添加解析后的数据
     * 8. 返回DataSupplier
     * 
     * 【示例1：Modbus TCP协议解析】
     * <pre>
     * @Override
     * public DataSupplier getDataSupplier(Protocol protocol, 
     *                                     ProtocolModelApiInvokeParam param, 
     *                                     Object value) {
     *     // 1. 转换为具体协议对象
     *     ModbusProtocol modbusProtocol = (ModbusProtocol) protocol;
     *     
     *     // 2. 从协议对象获取配置信息
     *     int unitId = modbusProtocol.getUnitId();        // 站号
     *     int functionCode = modbusProtocol.getFunctionCode();  // 功能码
     *     int startAddress = modbusProtocol.getStartAddress();  // 起始地址
     *     
     *     // 3. 获取设备标识
     *     DeviceKey deviceKey = param.getDeviceKey();
     *     
     *     // 4. 构建数据容器
     *     DataSupplier supplier = new DataSupplier(getProtocol().getCode(), deviceKey);
     *     
     *     // 5. 解析报文(Modbus RTU响应)
     *     byte[] data = (byte[]) value;
     *     int byteCount = data[2];    // 字节数
     *     
     *     // 6. 根据功能码和协议配置解析数据
     *     if(functionCode == 0x03) {
     *         // 读保持寄存器响应
     *         for(int i = 0; i < byteCount / 2; i++) {
     *             int address = startAddress + i;
     *             int regValue = ((data[3 + i*2] & 0xFF) << 8) | (data[4 + i*2] & 0xFF);
     *             
     *             // 使用协议配置的信息
     *             supplier.addValue("reg_" + address, "寄存器" + address, regValue);
     *         }
     *     }
     *     
     *     return supplier;
     * }
     * </pre>
     * 
     * 【示例2：JSON报文解析】
     * <pre>
     * @Override
     * public DataSupplier getDataSupplier(Protocol protocol, 
     *                                     ProtocolModelApiInvokeParam param, 
     *                                     Object value) {
     *     // 1. 转换为具体协议对象
     *     MqttProtocol mqttProtocol = (MqttProtocol) protocol;
     *     
     *     // 2. 从协议对象获取配置
     *     String topic = mqttProtocol.getTopic();          // MQTT主题
     *     String qos = mqttProtocol.getQos();              // QoS等级
     *     
     *     // 3. 获取设备标识
     *     DeviceKey deviceKey = param.getDeviceKey();
     *     
     *     // 4. 构建数据容器
     *     DataSupplier supplier = new DataSupplier(getProtocol().getCode(), deviceKey);
     *     
     *     // 5. 解析JSON报文
     *     JSONObject json = (JSONObject) value;
     *     
     *     // 6. 根据协议配置提取属性
     *     if(json.containsKey("temperature")) {
     *         // 使用协议中的单位配置
     *         String unit = mqttProtocol.getTemperatureUnit();  // 从协议获取单位
     *         supplier.addValue("temp", "温度(" + unit + ")", json.getDouble("temperature"));
     *     }
     *     if(json.containsKey("humidity")) {
     *         supplier.addValue("hum", "湿度(%)", json.getDouble("humidity"));
     *     }
     *     
     *     return supplier;
     * }
     * </pre>
     * 
     * 【注意事项】
     * - protocol参数包含完整协议信息，充分利用协议对象的数据
     * - 根据具体协议类型转换为对应的协议对象
     * - 报文解析失败时，返回空的DataSupplier(不要返回null)
     * - 注意数据类型转换和字节序处理
     * - 做好异常处理，避免影响其他设备
     * 
     * @param protocol 协议报文对象(可转换为具体协议对象)
     * @param param 调用参数(平台主动调用时传入，可能为null)
     * @param value 设备响应的原始数据
     * @return 采集数据容器
     */
    @Override
    public DataSupplier getDataSupplier(Protocol protocol, 
                                        ProtocolModelApiInvokeParam param, 
                                        Object value) {
        // TODO: 实现数据解析逻辑
        
        // 1. 转换为具体协议对象
        // XxxProtocol xxxProtocol = (XxxProtocol) protocol;
        // 从协议对象获取配置信息
        // int configValue = xxxProtocol.getConfigValue();
        
        // 2. 获取设备标识
        DeviceKey deviceKey = param != null ? param.getDeviceKey() : null;
        
        // 3. 创建数据容器
        DataSupplier supplier = new DataSupplier(getProtocol().getCode(), deviceKey);
        
        Object collectValue = dataResolver(attrMeta, value);

        // 添加数据
        dataSupplier.addValue(attrMeta.getId(), attrMeta.getAddress(), attrMeta.getField(), collectValue);
        
        // 5. 返回解析结果
        return supplier;
    }

    // ==================== 以下方法根据需要可选覆盖 ====================

    /**
     * 获取网关设备配置
     * 
     * 【说明】
     * 1. 返回网关设备级别的额外配置项
     * 2. 在管理端新增网关设备时显示这些配置项
     * 3. 与GatewayMetas配合使用，GatewayMetas是基本配置(主机、端口等)
     * 4. 此方法用于返回协议特定的额外配置
     * 5. 如果不需要额外配置，返回EMPTY_CONFIG或空数组
     * 
     * 【使用场景】
     * - 网关设备特定的通信参数(如重试次数、心跳间隔)
     * - 网关设备特定的协议参数(如协议版本、加密方式)
     * - 网关设备特定的业务参数(如数据上报频率)
     * 
     * 【与GatewayMetas的区别】
     * - GatewayMetas: 基本网络配置(主机、端口、超时等)，必须实现
     * - getGatewayConfig(): 额外协议配置，可选实现
     * 
     * 【示例：MQTT网关配置】
     * <pre>
     * @Override
     * public ParamMeta[] getGatewayConfig() {
     *     return new ParamMeta[]{
     *         ParamMeta.buildRequired("clientId", "客户端ID", "", "MQTT客户端标识"),
     *         ParamMeta.buildNumber("keepAlive", "心跳间隔(秒)", 60, "MQTT心跳间隔"),
     *         ParamMeta.buildSelect("qos", "QoS等级", "0",
     *             new String[]{"0", "1", "2"}, "消息质量等级")
     *     };
     * }
     * </pre>
     * 
     * @return 网关配置参数数组
     */
//    @Override
//    public ParamMeta[] getGatewayConfig() {
//        return GatewayMetas;
//    }

    /**
     * 获取子设备配置
     * 
     * 【说明】
     * 1. 返回子设备级别的额外配置项
     * 2. 在管理端配置网关下的子设备时显示这些配置项
     * 3. 默认返回EMPTY_CONFIG，无需覆盖
     * 
     * 【使用场景】
     * - 子设备特定的数据格式配置(如Modbus从机数据格式ABCD/DCBA)
     * - 子设备特定的地址配置(如Modbus从机站号)
     * 
     * 【示例：Modbus子设备配置】
     * <pre>
     * @Override
     * public ParamMeta[] getDeviceConfig() {
     *     return new ParamMeta[]{
     *         ParamMeta.buildRequiredNumber("slaveId", "从机站号", 1, "Modbus从机地址(1-247)"),
     *         ParamMeta.buildSelect("dataFormat", "数据格式", "ABCD",
     *             new String[]{"ABCD", "BADC", "CDAB", "DCBA"}, "寄存器字节序"),
     *         ParamMeta.buildNumber("readInterval", "读取间隔(ms)", 100, "连续读取间隔")
     *     };
     * }
     * </pre>
     * 
     * @return 子设备配置参数数组
     */
    // @Override
    // public ParamMeta[] getDeviceConfig() {
    //     return new ParamMeta[]{
    //         // TODO: 添加子设备特定配置
    //     };
    // }
}
