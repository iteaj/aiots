package com.iteaj.iboot.plugin.protocol.{协议简称};

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.iboot.plugin.protocol.{协议简称}.component.{协议简称}ServerComponent;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.data.ModelAttrMeta;
import com.iteaj.iot.data.ProtocolModelAttr;
import com.iteaj.iot.server.ServerComponent;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * {协议名称}协议提供 - 直连服务端模式
 * 
 * 【说明】
 * 1. 本类是{协议名称}协议驱动的核心入口类
 * 2. 继承DirectStaticServerProtocolSupplier实现直连服务端协议
 * 3. 适用于设备直连平台且平台主动采集数据的场景
 * 4. 与StaticServerProtocolSupplier的区别：
 *    - StaticServerProtocolSupplier: getDataSupplier接收原始报文(Object value)
 *    - DirectStaticServerProtocolSupplier: doGetDataSupplier接收已解析的模型属性(Map)
 * 5. 必须添加@Component注解注册为Spring Bean
 * 
 * 【泛型参数说明】
 * C - 网络配置类，使用NetworkConfigImpl的子类(如TcpNetworkConfig等)
 * T - 网络组件类，必须是ServerComponent的子类
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
public class {协议简称}ProtocolSupplier extends DirectStaticServerProtocolSupplier<
        NetworkConfigImpl.{网络配置类型},           // TODO: 替换为实际的网络配置类型
        {协议简称}ServerComponent> {                // TODO: 替换为实际的服务端组件类

    /**
     * 网关配置参数
     * 
     * 【说明】
     * 1. 定义在平台配置网关时需要填写的参数
     * 2. 用户在管理端添加网关设备时会看到这些配置项
     * 3. 服务端通常需要配置监听端口
     * 
     * 【参数构建方法】
     * - ParamMeta.buildRequired(field, name) - 必填字段
     * - ParamMeta.buildRequiredNumber(field, name, defaultValue, remark) - 必填数字
     * - ParamMeta.buildNumber(field, name, defaultValue, remark) - 选填数字
     */
    private ParamMeta[] GatewayMetas = new ParamMeta[]{
            // TODO: 根据协议实际要求配置网关参数
            ParamMeta.buildRequiredNumber("port", "监听端口", {默认端口}, "服务监听端口"),
            // ParamMeta.buildRequired("host", "绑定地址", "0.0.0.0", "留空则绑定所有网卡"),
            // ParamMeta.buildNumber("maxConnections", "最大连接数", 100, "同时最大连接数"),
    };

    /**
     * 构造函数
     * 
     * 【说明】
     * 1. 调用父类构造函数，传入协议码、传输协议类型、协议描述
     * 2. 协议码必须全局唯一，建议使用大写+下划线格式
     * 3. 传输协议类型参考TransportProtocol枚举
     * 
     * 【参数说明】
     * protocolCode - 协议码，全局唯一标识
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
     * 2. 建议格式：主版本.次版本.修订号
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
     * 创建服务端网络组件
     * 
     * 【说明】
     * 1. 创建并返回服务端网络组件实例
     * 2. 服务端组件负责监听端口，接受设备连接
     * 3. 此方法只在首次创建组件时调用
     * 4. 组件会被FrameworkManager自动注册和管理
     * 
     * 【实现要求】
     * - 创建并返回服务端组件实例
     * - 不要在方法中启动监听，由Framework统一管理
     * - 如果组件需要特殊初始化，在此处完成
     * 
     * @param config 网络配置，包含port等监听配置
     * @return 服务端网络组件实例
     */
    @Override
    protected {协议简称}ServerComponent doCreateComponent(NetworkConfigImpl.{网络配置类型} config) {
        // TODO: 创建并返回服务端组件实例
        return new {协议简称}ServerComponent();
    }


    // ==================== 以下方法根据需要可选覆盖 ====================

    /**
     * 获取协议提供的数据(核心方法)
     * 
     * 【说明】
     * 1. 这是DirectStaticServerProtocolSupplier的核心方法
     * 2. 与StaticServerProtocolSupplier的区别：
     *    - StaticServerProtocolSupplier.getDataSupplier()接收原始报文(Object value)
     *    - DirectStaticServerProtocolSupplier.doGetDataSupplier()接收已解析的模型属性(Map)
     * 3. 框架会自动将报文解析为模型属性Map，开发者只需处理属性值
     * 4. 此方法简化了数据解析流程，适合标准协议
     * 
     * 【参数说明】
     * protocol - 协议报文对象，包含设备的具体协议数据
     *            - 可以转换为具体的协议对象(如ModbusProtocol、MqttProtocol等)
     *            - 包含协议的所有配置信息和数据
     *            - 通过protocol.getXxx()获取协议特定数据
     * param - 调用参数，平台主动调用时传入，可能为null
     * metas - 已解析的模型属性Map
     *          Key: 属性ID(Serializable)
     *          Value: ModelAttrMeta(包含属性元数据和解析后的值)
     * 
     * 【返回说明】
     * DataSupplier - 采集数据容器
     * - 必须设置protocolCode和deviceKey
     * - 调用addValue(code, name, value)添加属性值
     * 
     * 【实现步骤】
     * 1. 将protocol转换为具体协议对象
     * 2. 从具体协议对象中获取协议配置
     * 3. 获取设备标识
     * 4. 创建DataSupplier对象
     * 5. 遍历metasMap，根据协议配置处理属性值
     * 6. 调用addValue()添加数据
     * 7. 返回DataSupplier
     * 
     * 【示例：使用协议配置处理数据】
     * <pre>
     * @Override
     * protected DataSupplier doGetDataSupplier(Protocol protocol, 
     *                                          ProtocolModelApiInvokeParam param, 
     *                                          Map<Serializable, ModelAttrMeta> metas) {
     *     // 1. 转换为具体协议对象
     *     ModbusProtocol modbusProtocol = (ModbusProtocol) protocol;
     *     
     *     // 2. 从协议对象获取配置
     *     String dataFormat = modbusProtocol.getDataFormat();  // 数据格式(ABCD/DCBA)
     *     int unitId = modbusProtocol.getUnitId();             // 站号
     *     
     *     // 3. 获取设备标识
     *     DeviceKey deviceKey = param.getDeviceKey();
     *     
     *     // 4. 创建数据容器
     *     DataSupplier supplier = new DataSupplier(getProtocol().getCode(), deviceKey);
     *     
     *     // 5. 遍历已解析的属性，根据协议配置处理
     *     for(Map.Entry<Serializable, ModelAttrMeta> entry : metas.entrySet()) {
     *         ModelAttrMeta attrMeta = entry.getValue();
     *         String field = attrMeta.getField();           // 属性代码
     *         String name = attrMeta.getName();             // 属性名称
     *         Object value = attrMeta.getValue();           // 属性值
     *         
     *         // 根据协议配置处理数据(如字节序转换)
     *         Object processedValue = processDataByFormat(value, dataFormat);
     *         
     *         // 添加数据
     *         supplier.addValue(field, name, processedValue);
     *     }
     *     
     *     return supplier;
     * }
     * </pre>
     * 
     * 【与StaticServerProtocolSupplier的对比】
     * <pre>
     * // StaticServerProtocolSupplier: 需要手动解析报文
     * @Override
     * public DataSupplier getDataSupplier(Protocol protocol, 
     *                                     ProtocolModelApiInvokeParam param, 
     *                                     Object value) {
     *     // 转换为具体协议对象
     *     ModbusProtocol modbusProtocol = (ModbusProtocol) protocol;
     *     int functionCode = modbusProtocol.getFunctionCode();
     *     
     *     byte[] data = (byte[]) value;  // 原始报文
     *     // 手动解析报文...
     *     return supplier;
     * }
     * 
     * // DirectStaticServerProtocolSupplier: 框架已解析，直接使用
     * @Override
     * protected DataSupplier doGetDataSupplier(Protocol protocol, 
     *                                          ProtocolModelApiInvokeParam param, 
     *                                          Map<Serializable, ModelAttrMeta> metas) {
     *     // 转换为具体协议对象
     *     ModbusProtocol modbusProtocol = (ModbusProtocol) protocol;
     *     String dataFormat = modbusProtocol.getDataFormat();
     *     
     *     // metas已包含解析后的属性
     *     for(ModelAttrMeta attr : metas.values()) {
     *         // 根据协议配置处理数据
     *         Object value = processDataByFormat(attr.getValue(), dataFormat);
     *         supplier.addValue(attr.getField(), attr.getName(), value);
     *     }
     *     return supplier;
     * }
     * </pre>
     * 
     * @param protocol 协议报文对象(可转换为具体协议对象)
     * @param param 调用参数(平台主动调用时传入，可能为null)
     * @param metas 已解析的模型属性Map
     * @return 采集数据容器
     */
    @Override
    protected DataSupplier doGetDataSupplier(Protocol protocol, 
                                             ProtocolModelApiInvokeParam param, 
                                             Map<Serializable, ModelAttrMeta> metas) {
        // TODO: 实现数据解析逻辑
        
        // 1. 获取设备标识
        DeviceKey deviceKey = param != null ? param.getDeviceKey() : null;
        
        // 2. 创建数据容器
        DataSupplier supplier = new DataSupplier(getProtocol().getCode(), deviceKey);
        
        // 3. 遍历已解析的模型属性
        if(metas != null && !metas.isEmpty()) {
            for(Map.Entry<Serializable, ModelAttrMeta> entry : metas.entrySet()) {
                ModelAttrMeta attrMeta = entry.getValue();

                Object collectValue = dataResolver(attrMeta, value);
                // 添加数据
                dataSupplier.addValue(attrMeta.getId(), attrMeta.getAddress(), attrMeta.getField(), collectValue);
        }
        
        // 4. 返回解析结果
        return supplier;
    }

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
     *             new String[]{"ABCD", "BADC", "CDAB", "DCBA"}, "寄存器字节序")
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
